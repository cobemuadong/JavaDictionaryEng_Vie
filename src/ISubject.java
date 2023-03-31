public interface ISubject {

    void notifyAllObserver(int type);

    void attach(IObserver observer);
    void detach(IObserver observer);
}
