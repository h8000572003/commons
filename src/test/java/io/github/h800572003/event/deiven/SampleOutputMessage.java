package io.github.h800572003.event.deiven;

public class SampleOutputMessage extends Message {

    private final int result;

    public SampleOutputMessage(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }
}
