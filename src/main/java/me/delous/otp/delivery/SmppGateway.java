package me.delous.otp.delivery;

import java.nio.charset.StandardCharsets;
import me.delous.otp.config.SmppProperties;
import me.delous.otp.otp.DeliveryRoute;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SmppGateway implements DeliveryGateway {
    private static final Logger log = LoggerFactory.getLogger(SmppGateway.class);
    private final SmppProperties properties;

    public SmppGateway(SmppProperties properties) {
        this.properties = properties;
    }

    @Override
    public DeliveryRoute route() {
        return DeliveryRoute.SMS;
    }

    @Override
    public void deliver(String target, String code) {
        SMPPSession session = new SMPPSession();
        try {
            BindParameter bindParameter = new BindParameter(
                    BindType.BIND_TX,
                    properties.systemId(),
                    properties.password(),
                    properties.systemType(),
                    TypeOfNumber.UNKNOWN,
                    NumberingPlanIndicator.UNKNOWN,
                    properties.sourceAddr()
            );
            session.connectAndBind(properties.host(), properties.port(), bindParameter);
            session.submitShortMessage(
                    properties.systemType(),
                    TypeOfNumber.UNKNOWN,
                    NumberingPlanIndicator.UNKNOWN,
                    properties.sourceAddr(),
                    TypeOfNumber.UNKNOWN,
                    NumberingPlanIndicator.UNKNOWN,
                    target,
                    new ESMClass(),
                    (byte) 0,
                    (byte) 1,
                    null,
                    null,
                    new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
                    (byte) 0,
                    new GeneralDataCoding(Alphabet.ALPHA_DEFAULT),
                    (byte) 0,
                    ("Ваш код: " + code).getBytes(StandardCharsets.UTF_8)
            );
            log.info("delivery.smpp.sent target={}", target);
        } catch (Exception ex) {
            throw new IllegalStateException("Не удалось отправить SMS через SMPP", ex);
        } finally {
            session.unbindAndClose();
        }
    }
}
