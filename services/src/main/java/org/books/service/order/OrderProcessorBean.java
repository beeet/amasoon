package org.books.service.order;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.books.persistence.order.Order;

@MessageDriven(mappedName = "jms/orderQueue", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class OrderProcessorBean implements MessageListener {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private TimerService timerService;

    @Override
    public void onMessage(Message message) {
        try {
            MapMessage mapMessage = (MapMessage) message;
            Integer orderId = mapMessage.getInt("orderId");
            Order order = em.find(Order.class, orderId);
            timerService.createSingleActionTimer(20000, new TimerConfig(order, true));
        } catch (JMSException ex) {
            Logger.getLogger(OrderProcessorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Timeout
    public void setStateToClose(Timer timer) {
        Order order = (Order) timer.getInfo();
        order = em.merge(order);
        if (order.isOpen()) {
            order.setStatus(Order.Status.closed);
        }
    }
    
}
