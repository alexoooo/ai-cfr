package ao.ai.cfr.abs;

/**
 *
 */
public interface AbstractionBuilder<InformationSet>
{
    byte[] infoDigest(InformationSet info);
}
