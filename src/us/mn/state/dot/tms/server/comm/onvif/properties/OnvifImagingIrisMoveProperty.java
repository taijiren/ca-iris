package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.FloatRange;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.SetImagingSettings;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.SetImagingSettingsResponse;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.ServiceNotSupportedException;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.SessionNotStartedException;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.SoapTransmissionException;

import java.io.IOException;

/**
 * A class that makes continuous iris movements from the absolute iris movement
 * interface provided by ONVIF.
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifImagingIrisMoveProperty extends OnvifProperty
	implements Runnable
{
	/**
	 * The granularity of individual requests. A larger number means
	 * the stepping of iris movements will be more smooth. A smaller number
	 * will mean larger jumps and a stepped feel to iris movements.
	 * Since we must receive a response from the device before we can
	 * proceed to the next request, this does not represent a linear
	 * relationship. A larger number will mean more requests which are
	 * effectively slower.
	 */
	private static final float GRANULARITY_OF_MOVEMENT = 50;
	private final DeviceRequest req;

	public OnvifImagingIrisMoveProperty(
		OnvifSessionMessenger session, DeviceRequest r)
	{
		super(session);
		this.req = r;
	}

	@Override
	protected void encodeStore() throws IOException {
		if (!supportsIrisMove())
			logFailure("Device does not support Iris Move. ");
		switch (req) {
		case CAMERA_IRIS_CLOSE:
		case CAMERA_IRIS_OPEN:
			session.startMovingIris(this);
			break;
		case CAMERA_IRIS_STOP:
			session.stopMovingIris();
			break;
		default:
			logFailure("Unexpected: " + req);
		}
	}

	private boolean supportsIrisMove()
		throws SoapTransmissionException, ServiceNotSupportedException,
		SessionNotStartedException
	{
		boolean supported = true;
		if (session.getImagingOptions() == null
			|| session.getImagingOptions().getExposure() == null
			|| session.getImagingOptions().getExposure()
			.getIris() == null
			// ONVIF states that min == max is indicative of
			// unsupported iris move
			|| session.getImagingOptions().getExposure().getIris()
			.getMin()
			!= session.getImagingOptions().getExposure().getIris()
			.getMax()
			|| session.getImagingSettings() == null
			|| session.getImagingSettings().getExposure() == null
			|| session.getImagingSettings().getExposure()
			.getIris() == null)
			supported = false;
		return supported;
	}

	/**
	 * Note that ONVIF iris commands are specified in
	 * decibels of light attenuation.
	 * A 0 dB iris value means that the iris is full opened.
	 * Typically this range is from 0 to 100 dB.
	 */
	@Override
	public void run() {
		try {
			final FloatRange range = session.getImagingOptions()
				.getExposure().getIris();
			final float min = range.getMin();
			final float max = range.getMax();
			final float incr = (max - min) / GRANULARITY_OF_MOVEMENT;
			float val;
			SetImagingSettings setReq = new SetImagingSettings();
			setReq.setVideoSourceToken(
				session.getMediaProfileTok());
			while (true) {
				val = session.getImagingSettings()
					.getExposure().getIris();
				val += (req == DeviceRequest.CAMERA_IRIS_OPEN ?
					-1 : 1) * incr;
				// if iris is fully opened
				if (req == DeviceRequest.CAMERA_IRIS_OPEN
					&& val < min) {
					updateVal(min, setReq);
					break;
				}
				// else if iris is fully closed
				else if (req == DeviceRequest.CAMERA_IRIS_CLOSE
					&& val > max) {
					updateVal(max, setReq);
					break;
				}
				else
					updateVal(val, setReq);
			}
		} catch (IOException e) {
			e.printStackTrace();
			log(e.getMessage());
		}
	}

	private void updateVal(float val, SetImagingSettings request)
		throws SessionNotStartedException, SoapTransmissionException,
		ServiceNotSupportedException
	{
		session.getImagingSettings().getExposure().setIris(val);
		request.setImagingSettings(session.getImagingSettings());
		response = session.makeRequest(request,
			SetImagingSettingsResponse.class);
	}
}