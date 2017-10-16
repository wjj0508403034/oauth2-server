package tech.tgls.mms.auth.common;


import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.AbstractUUIDGenerator;
import org.hibernate.id.Configurable;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.Serializable;
import java.util.Properties;
import java.util.UUID;


/**
 * Created by shenyuyang on 2016/11/8.
 */
public class IdGenerator extends AbstractUUIDGenerator implements Configurable {

    private static final Logger logger = LoggerFactory.getLogger(IdGenerator.class);



    @Override
    public void configure(Type type, Properties properties, ServiceRegistry serviceRegistry) throws MappingException {
    }

    @Override
    public Serializable generate(SessionImplementor sessionImplementor, Object o) throws HibernateException {

//        WebPropConfig webPropConfig = SpringCtx.getBeanByClass(WebPropConfig.class);
//
//        logger.info("getDataCenterId="+ webPropConfig.getDataCenterId());
//
//        IdWorker idWorker = new IdWorker(0, webPropConfig.getDataCenterId());
//
//        return idWorker.nextId();
        UUID uuid = UUID.randomUUID();
        long bits = uuid.getMostSignificantBits();
        if (bits < 0)
        {
            return -bits;
        }
        return bits;
    }
}

