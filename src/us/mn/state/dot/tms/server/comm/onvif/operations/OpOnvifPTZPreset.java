package us.mn.state.dot.tms.server.comm.onvif.operations;

import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.OpOnvif;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZNodesProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZPresetRecallProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZPresetStoreProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZStopProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;

import java.io.IOException;

/**
 * An OpOnvifPTZ sends OnvifPTZPreset*Properties to the PTZ Service.
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OpOnvifPTZPreset extends OpOnvif<OnvifProperty> {
	private boolean store;
	private int preset;

	public OpOnvifPTZPreset(
		CameraImpl c, int preset, boolean store,
		OnvifSessionMessenger session, ControllerImpl controller)
	{
		super(PriorityLevel.COMMAND, c, session, OnvifService.PTZ, controller);
		this.preset = preset;
		this.store = store;
	}

	@Override
	protected OnvifPhase phaseTwo() {
		return new NodesPhase();
	}

	protected class NodesPhase extends OnvifPhase {
		@Override
		protected OnvifProperty selectProperty() throws IOException {
			return session.getNodes() == null ?
				new OnvifPTZNodesProperty(session) : null;
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			return store ? new EnsureStoppedPhase() : new RecallPhase();
		}
	}

	/**
	 * Onvif devices must be in a stopped state in order to store preset
	 */
	protected class EnsureStoppedPhase extends OnvifPhase {
		@Override
		protected OnvifProperty selectProperty() throws IOException {
			return new OnvifPTZStopProperty(session);
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			return new StorePhase();
		}
	}

	protected class StorePhase extends OnvifPhase {
		@Override
		protected OnvifProperty selectProperty() throws IOException {
			return new OnvifPTZPresetStoreProperty(session,
				preset, session.getNodes());
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			return null;
		}
	}

	protected class RecallPhase extends OnvifPhase {
		@Override
		protected OnvifProperty selectProperty() throws IOException {
			return new OnvifPTZPresetRecallProperty(session, preset,
				session.getNodes());
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			return null;
		}
	}
}
