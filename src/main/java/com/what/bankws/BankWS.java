
package com.what.bankws;

import java.io.StringWriter;
import java.util.Random;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;



    @WebService(serviceName="InterestRateService")
    public class BankWS {

        @WebMethod(operationName = "getInterestRate")
        public String getInterestRate(@WebParam(name = "ssn") String ssn, 
                @WebParam(name = "creditScore") int creditScore, @WebParam(name = "loanAmount") float loanAmount,@WebParam(name = "loanDuration") int loanDuration) throws JAXBException {
           
            Random ran = new Random();
            int intReply=ran.nextInt(1000 - 100 + 1) + 100;           
            double reply=intReply/100d;
            BankResponse res=new BankResponse(reply, ssn);
            JAXBContext jc = JAXBContext.newInstance(BankResponse.class);
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            JAXBElement<BankResponse> je2 = new JAXBElement(new QName("LoanRequest"), BankResponse.class, res);
            StringWriter sw = new StringWriter();
            marshaller.marshal(je2, sw);
            String xmlString = sw.toString();
            return xmlString;
        }

    }

