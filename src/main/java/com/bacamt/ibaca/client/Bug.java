package com.bacamt.ibaca.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import java.util.function.Consumer;
import java.util.function.Function;

public class Bug implements EntryPoint {

    @Override public void onModuleLoad() {
        RootPanel root = RootPanel.get();

        // Rx.of(create()).map(JavaScriptObject::<OverlayFoo>cast)
        //         .map(new Function<OverlayFoo, String>() { public String apply(OverlayFoo foo) {return Bug.this.foo(foo);} })
        //         .map(new Function<String, Label>() { public Label apply(String text) {return new Label(text);} })
        //         .subscribe(new Consumer<Label>() { public void accept(Label w) { root.add(w); } });

        Rx.of(create()).map(JavaScriptObject::<OverlayFoo>cast)
                .map(this::foo)
                .map(Label::new)
                .subscribe(root::add);
    }

    static class Rx<T> {
        private final Producer<T> p;

        private Rx(Producer<T> s) {this.p = s;}

        public final <R> Rx<R> lift(Operator<? extends R, ? super T> operator) {
            return create(o -> p.subscribe(operator.call(o)));
        }

        public final <R> Rx<R> map(Function<? super T, ? extends R> transformer) {
            return lift(o -> t -> o.accept(transformer.apply(t)));
        }

        public final void subscribe(Consumer<T> s) {
            this.p.subscribe(s);
        }

        public static <R> Rx<R> of(R... rs) {
            return create(s -> {
                for (R r : rs) {
                    s.accept(r);
                }
            });
        }

        public static <R> Rx<R> create(Producer<R> s) {
            return new Rx<>(s);
        }
    }

    public interface Producer<T> {
        void subscribe(Consumer<? super T> s);
    }

    public interface Operator<R, T> {
        Consumer<? super T> call(Consumer<? super R> t);
    }

    public String foo(Foo foo) { return foo.getFoo(); }

    public static native JavaScriptObject create() /*-{
        return {foo: "success"};
    }-*/;

    public interface Foo {
        String getFoo();
    }

    public static class OverlayFoo extends JavaScriptObject implements Foo {
        protected OverlayFoo() {}

        public final native String getFoo() /*-{
            return this.foo;
        }-*/;
    }
}
