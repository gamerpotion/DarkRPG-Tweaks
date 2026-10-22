package org.spongepowered.asm.mixin.injection.callback;
public class CallbackInfoReturnable<T> {
    private T value;
    private boolean written;
    public void setReturnValue(T value) { this.value = value; written=true; }
    public T getReturnValue() { return value; }
    public boolean wasWritten() { return written; }
}
