package ao.ai.cfr.abs;

/**
 *
 */
public interface AbstractionDomainBuilder<T>
{
    void add(T view);

    AbstractionDomain<T> build();
}
