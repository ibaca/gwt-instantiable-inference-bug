package com.bacamt.ibaca.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class Bug implements EntryPoint {

    @Override public void onModuleLoad() {
        RootPanel root = RootPanel.get();

        // Rx.of(create()).map(JavaScriptObject::<OverlayFoo>cast)
        //         .map(new Func<OverlayFoo, String>() { public String f(OverlayFoo foo) {return Bug.this.foo(foo);} })
        //         .map(new Func<String, Label>() { public Label f(String text) {return new Label(text);} })
        //         .subscribe(new Subscriber<Label>() { public void onNext(Label w) { root.add(w); } });

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

        public final <R> Rx<R> map(Func<? super T, ? extends R> transformer) {
            return lift((Operator<R, T>) o -> t -> o.onNext(transformer.f(t)));
        }

        public final void subscribe(Subscriber<T> s) {
            this.p.subscribe(s);
        }

        public static <R> Rx<R> of(R... rs) {
            return create(s -> {
                for (R r : rs) {
                    s.onNext(r);
                }
            });
        }

        public static <R> Rx<R> create(Producer<R> s) {
            return new Rx<>(s);
        }
    }

    interface Producer<T> {
        void subscribe(Subscriber<? super T> s);
    }

    interface Subscriber<T> {
        void onNext(T next);
    }

    public interface Operator<R, T> {
        Subscriber<? super T> call(Subscriber<? super R> t);
    }

    interface Func<T, V> {
        V f(T t);
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
