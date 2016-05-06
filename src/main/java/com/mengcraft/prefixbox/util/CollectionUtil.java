package com.mengcraft.prefixbox.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created on 16-3-9.
 */
public final class CollectionUtil {

    @SuppressWarnings("unchecked")
    public static <E> Collection<E> concat(Collection<E> i, Collection<E> i1) {
        Collection<E> out = new ArrayList<>(i.size() + i1.size());
        out.addAll(i);
        out.addAll(i1);
        return out;
    }

    public static <E> int count(Collection<E> i, Object obj) {
        int count = 0;
        for (E e : i) {
            if (e.equals(obj)) {
                count++;
            }
        }
        return count;
    }

    public static <E> void forEach(Collection<E> i, Predicate<E> p, Consumer<E> c) {
        for (E e : i) {
            if (p.test(e)) {
                c.accept(e);
            }
        }
    }

    public static <T> void forEachRemaining(Iterator<T> i, Predicate<T> p, Consumer<T> c) {
        i.forEachRemaining(t -> {
            if (p.test(t)) {
                c.accept(t);
            }
        });
    }

    public static <T> Collection<T> reduce(Collection<T> in, Predicate<T> p) {
        List<T> out = new ArrayList<>();
        forEach(in, p, t -> {
            out.add(t);
        });
        return out;
    }

}
