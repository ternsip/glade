package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.LazyWrapper;
import com.ternsip.glade.universe.common.Balance;

public interface IBalance {

    LazyWrapper<Balance> BALANCE = new LazyWrapper<>(Balance::new);

    default Balance getBalance() {
        return BALANCE.getObjective();
    }

}
