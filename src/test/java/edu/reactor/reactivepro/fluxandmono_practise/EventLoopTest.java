package edu.reactor.reactivepro.fluxandmono_practise;


import org.junit.jupiter.api.Test;

public class EventLoopTest {

    @Test
    public void noOfProcessors(){

        System.out.println(Runtime.getRuntime().availableProcessors());
    }
}
