package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.LazyWrapper;
import com.ternsip.glade.universe.bindings.Bindings;

public interface IBindings {

    LazyWrapper<Bindings> BINDINGS = new LazyWrapper<>(Bindings::new);

    default Bindings getBindings() {
        return BINDINGS.getObjective();
    }

}
