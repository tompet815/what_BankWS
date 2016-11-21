package com.what.bankws;

import com.rabbitmq.client.AMQP.*;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.namespace.QName;

@WebService(serviceName = "InterestRateService")
public class BankWS {

    @WebMethod(operationName = "getInterestRate")
    public String getInterestRate(@WebParam(name = "ssn") String ssn,
            @WebParam(name = "creditScore") int creditScore,
            @WebParam(name = "loanAmount") double loanAmount,
            @WebParam(name = "loanDuration") int loanDuration,
            @WebParam(name = "replyTo") String replyTo,
            @WebParam(name = "corrId") String corrId,
            @WebParam(name = "bankName") String bankName,
            @WebParam(name = "total") int total,
            @WebParam(name = "messageNo") int messageNo) {

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("datdb.cphbusiness.dk");
            factory.setUsername("what");
            factory.setPassword("what");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            double reply = 0;
            if (creditScore <= 100) {
                reply = 12.02301;
            }
            else if (creditScore <= 150 && creditScore > 100) {
                reply = 10.20155;
            }
            else if (creditScore <= 200 && creditScore > 150) {
                reply = 9.2335;
            }
            else {
                reply = 6.32265;
            }

            Map<String, Object> headers = new HashMap();
            headers.put("bankName", bankName);
            headers.put("total", total);
            headers.put("messageNo", messageNo);
            BasicProperties prop=propBuilder(corrId,headers);
            BankResponse res = new BankResponse(reply, ssn);
            JAXBContext jc = JAXBContext.newInstance(BankResponse.class);
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            JAXBElement<BankResponse> je2 = new JAXBElement(new QName("LoanResponse"), BankResponse.class, res);
            StringWriter sw = new StringWriter();
            marshaller.marshal(je2, sw);
            String xmlString = sw.toString();
            channel.queueDeclare(replyTo, false, false, false, null);
            channel.basicPublish("", replyTo, prop, xmlString.getBytes());
            System.out.println("xml" + xmlString);
            return xmlString;
        }
        catch (PropertyException ex) {
            Logger.getLogger(BankWS.class.getName()).log(Level.SEVERE, null, ex);
            return (ex.getMessage());
        }
        catch (JAXBException ex) {
            Logger.getLogger(BankWS.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        }
        catch (IOException ex) {
            Logger.getLogger(BankWS.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        }
    }

    private BasicProperties propBuilder(String corrId, Map<String, Object> headers) {
        BasicProperties.Builder builder = new BasicProperties.Builder();
        builder.correlationId(corrId);
        builder.headers(headers);
        BasicProperties prop = builder.build();
        return prop;
    }

}
