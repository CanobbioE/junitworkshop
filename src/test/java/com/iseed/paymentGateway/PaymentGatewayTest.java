package com.iseed.paymentGateway;

import com.iseed.paymentGateway.circuits.Circuit;
import com.iseed.paymentGateway.circuits.CreditCardCircuit;
import com.iseed.paymentGateway.circuits.PaypalCircuit;
import com.iseed.paymentGateway.domain.Amount;
import com.iseed.paymentGateway.domain.Order;
import com.iseed.paymentGateway.domain.OrderItem;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

public class PaymentGatewayTest {

    @Rule
    public JUnitRuleMockery ctx = new JUnitRuleMockery();
    private PaypalCircuit paypalMock = ctx.mock(PaypalCircuit.class);
    private CreditCardCircuit creditMock = ctx.mock(CreditCardCircuit.class);


    @Test(expected = IllegalArgumentException.class)
    public void paymentGatewayPayShouldThrowAnExceptionWhenAmountIsZero() {
        PaymentGateway paymentGateway = new PaymentGateway(null, null);
        paymentGateway.pay(new Amount(BigDecimal.ZERO, null), null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void paymentGatewayPayShouldThrowAnExceptionWhenAmountIsNegative() {
        PaymentGateway paymentGateway = new PaymentGateway(null, null);
        paymentGateway.pay(new Amount(BigDecimal.valueOf(-42), null), null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void paymentGatewayPayShouldThrowAnExceptionWhenCurrencyIsNotEUR() {
        PaymentGateway paymentGateway = new PaymentGateway(null, null);
        paymentGateway.pay(new Amount(BigDecimal.ONE, Currency.getInstance("USD")), null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void paymentGatewayPayShouldThrowAnExceptionWhenOrderIsEmpty() {
        PaymentGateway paymentGateway = new PaymentGateway(null, null);
        paymentGateway.pay(new Amount(BigDecimal.ONE, Currency.getInstance("EUR")), new Order(new ArrayList<OrderItem>()), null);
    }


    @Test
    public void paymentGatewayPayShouldSuccessGivenPaypalCircuit() {
       final Amount amount = new Amount(BigDecimal.ONE, Currency.getInstance("EUR"));
        ctx.checking(new Expectations() {{
                oneOf(paypalMock).pay(amount);
                will(returnValue(true));
            }});
        PaymentGateway paymentGateway = new PaymentGateway(paypalMock, null);
        Order order = new Order(Arrays.asList(new OrderItem("1", 1)));
        Assert.assertNotNull(paymentGateway.pay(amount, order, Circuit.PAYPAL));
    }

    @Test
    public void paymentGatewayPayShouldNotSuccessGivenPaypalCircuit() {
       final Amount amount = new Amount(BigDecimal.ONE, Currency.getInstance("EUR"));
        ctx.checking(new Expectations() {{
                oneOf(paypalMock).pay(amount);
                will(returnValue(false));
            }});
        PaymentGateway paymentGateway = new PaymentGateway(paypalMock, null);
        Order order = new Order(Arrays.asList(new OrderItem("1", 1)));
        Assert.assertNull(paymentGateway.pay(amount, order, Circuit.PAYPAL));
    }

    @Test
    public void paymentGatewayPayShouldSuccessGivenCreditCardlCircuit() {
       final Amount amount = new Amount(BigDecimal.ONE, Currency.getInstance("EUR"));
        ctx.checking(new Expectations() {{
                oneOf(creditMock).pay(amount);
                will(returnValue(true));
            }});
        PaymentGateway paymentGateway = new PaymentGateway(paypalMock, creditMock);
        Order order = new Order(Arrays.asList(new OrderItem("1", 1)));
        Assert.assertNotNull(paymentGateway.pay(amount, order, Circuit.CREDIT_CARD));
    }

    @Test
    public void paymentGatewayPayShouldNotSuccessGivenCreditCardlCircuit() {
       final Amount amount = new Amount(BigDecimal.ONE, Currency.getInstance("EUR"));
        ctx.checking(new Expectations() {{
                oneOf(creditMock).pay(amount);
                will(returnValue(false));
            }});
        PaymentGateway paymentGateway = new PaymentGateway(paypalMock, creditMock);
        Order order = new Order(Arrays.asList(new OrderItem("1", 1)));
        Assert.assertNull(paymentGateway.pay(amount, order, Circuit.CREDIT_CARD));
    }

}
