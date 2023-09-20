package io.github.ithamal.beanfetch.fetcher.enhance;

import io.github.ithamal.beanfetch.fetcher.FetchCallback;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.*;


/**
 * @author: ken.lin
 * @date: 2023-09-20 15:31
 */
@SuppressWarnings("unchecked")
public class ListProxy<E> implements List<E> {

    private FetchCallback<?, ?> fetchCallback;

    private Method refGetter;

    private volatile List<E> target;

    public ListProxy(FetchCallback<?, ?> fetchCallback, Method refGetter) {
        this.fetchCallback = fetchCallback;
        this.refGetter = refGetter;
    }

    @SneakyThrows
    private List<E> getTarget() {
        if (target != null) {
            return target;
        }
        synchronized (this) {
            if (target != null) {
                return target;
            }
            Object source = fetchCallback.fetch();
            target = (List<E>) this.refGetter.invoke(source);
            if (target != null) {
                fetchCallback = null;
                refGetter = null;
            }
        }
        return target == null ? Collections.emptyList() : target;
    }

    @Override
    public int size() {
        return getTarget().size();
    }

    @Override
    public boolean isEmpty() {
        return getTarget().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getTarget().contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return getTarget().iterator();
    }

    @Override
    public Object[] toArray() {
        return getTarget().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getTarget().toArray(a);
    }

    @Override
    public boolean add(E e) {
        return getTarget().add(e);
    }

    @Override
    public boolean remove(Object o) {
        return getTarget().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getTarget().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return getTarget().addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return getTarget().addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return getTarget().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return getTarget().retainAll(c);
    }

    @Override
    public void clear() {
        getTarget().clear();
    }

    @Override
    public E get(int index) {
        return getTarget().get(index);
    }

    @Override
    public E set(int index, E element) {
        return getTarget().set(index, element);
    }

    @Override
    public void add(int index, E element) {
        getTarget().add(index, element);
    }

    @Override
    public E remove(int index) {
        return getTarget().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return getTarget().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getTarget().lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return getTarget().listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return getTarget().listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return getTarget().subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        return getTarget().toString();
    }
}
