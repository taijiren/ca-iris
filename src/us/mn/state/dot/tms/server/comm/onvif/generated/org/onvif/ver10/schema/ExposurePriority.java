
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <prop>Java class for ExposurePriority.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * <prop>
 * <pre>
 * &lt;simpleType name="ExposurePriority"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="LowNoise"/&gt;
 *     &lt;enumeration value="FrameRate"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ExposurePriority")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public enum ExposurePriority {

    @XmlEnumValue("LowNoise")
    LOW_NOISE("LowNoise"),
    @XmlEnumValue("FrameRate")
    FRAME_RATE("FrameRate");
    private final String value;

    ExposurePriority(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ExposurePriority fromValue(String v) {
        for (ExposurePriority c: ExposurePriority.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
