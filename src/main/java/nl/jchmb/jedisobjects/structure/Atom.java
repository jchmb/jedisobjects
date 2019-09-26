package nl.jchmb.jedisobjects.structure;

public interface Atom<E> extends Deletable {
	public E get();
	public void set(E e);
}
