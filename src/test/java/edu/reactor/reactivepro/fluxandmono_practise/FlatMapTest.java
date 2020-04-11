package edu.reactor.reactivepro.fluxandmono_practise;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FlatMapTest {
    private List<String> list = Arrays.asList("A", "B", "C", "D", "E", "F");

    @Test
    public void fluxTest_mapData() {
        Flux<String> fluxString = Flux.fromIterable(list)
                .map(String::toLowerCase)
                .log();
        StepVerifier.create(fluxString)
                .expectNext("a","b","c","d","e","f")
                .verifyComplete();
    }

    /** we use the flatMap when we want to do a external DB call or api call
     * for every next iterable and in that case api or db returns the flux of element
     * and all the elements are called by main thread only
     * Total time also taken is 6 sec.
     */
    @Test
    public void fluxTest_flatMap_sequentialCall() {
        Flux<String> stringFlux = Flux.fromIterable(list)
                .map(String::toLowerCase)
                .flatMap(s -> Flux.fromIterable(getListOfName(s)))
                .log();
         StepVerifier.create(stringFlux)
         .expectNextCount(12)
         .verifyComplete();
    }

    @Test
    public void fluxTest_flatMap_parallelCall(){
        Flux<String> stringFlux = Flux.fromIterable(list)
             //   .flatMap(s -> Flux.fromIterable(getListOfName(s)).parallel())   // wrong method call of parallel
                //window gives the buffer of 2 element here, it will hold the iteration till 2 element buffer and than proceed
                    // that's why buffer return Flux<Flux<String>>
                .window(2)
                .flatMap(fs-> fs.map(this::getListOfName).subscribeOn(Schedulers.parallel()))
                .flatMap(Flux::fromIterable)
                .log();
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void fluxTest_flatMapSequential_parallelCall(){
        Flux<String> stringFlux = Flux.fromIterable(list)
                //   .flatMap(s -> Flux.fromIterable(getListOfName(s)).parallel())   // wrong method call of parallel
                //window gives the buffer of 2 element here, it will hold the iteration till 2 element buffer and than proceed
                // that's why buffer return Flux<Flux<String>>
                .window(2)
                //sequence of the element will be maintained
                .flatMapSequential(fs-> fs.map(this::getListOfName).subscribeOn(Schedulers.parallel()))
                .flatMap(Flux::fromIterable)
                .log();
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> getListOfName(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, s+" Ravi");
    }
}
