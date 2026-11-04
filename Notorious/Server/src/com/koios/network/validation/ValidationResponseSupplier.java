package com.koios.network.validation;

import com.koios.network.Session;

public interface ValidationResponseSupplier {

	public ValidationResponse fetch(Session client);

}
