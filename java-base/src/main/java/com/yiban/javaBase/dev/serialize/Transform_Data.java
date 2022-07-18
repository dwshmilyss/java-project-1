package com.yiban.javaBase.dev.serialize;

import java.util.HashSet;
import scala.Option;
import scala.Product;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Iterable;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

public class Transform_Data<U> implements Product, Serializable {
    private String tenant_pk;
    private Iterable<Tuple2<U, HashSet<String>>> tenant_data;

    public Transform_Data() {
    }


    public String productPrefix() {
        return "Transform_Data";
    }

    public int productArity() {
        return 2;
    }

    public Object productElement(int x$1) {
        int i = x$1;
        switch (i) {
            case 0:

            case 1:

        }
        throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
    }

    public Iterator<Object> productIterator() {
        return ScalaRunTime$.MODULE$.typedProductIterator(this);
    }

    public boolean canEqual(Object x$1) {
        return x$1 instanceof Transform_Data;
    }

    public int hashCode() {
        return ScalaRunTime$.MODULE$._hashCode(this);
    }

    public String toString() {
        return ScalaRunTime$.MODULE$._toString(this);
    }

    public Transform_Data(String tenant_pk, Iterable<Tuple2<U, HashSet<String>>> tenant_data) {
        this.tenant_pk = tenant_pk;
        this.tenant_data = tenant_data;
    }

    public String getTenant_pk() {
        return tenant_pk;
    }

    public void setTenant_pk(String tenant_pk) {
        this.tenant_pk = tenant_pk;
    }

    public Iterable<Tuple2<U, HashSet<String>>> getTenant_data() {
        return tenant_data;
    }

    public void setTenant_data(Iterable<Tuple2<U, HashSet<String>>> tenant_data) {
        this.tenant_data = tenant_data;
    }
}
