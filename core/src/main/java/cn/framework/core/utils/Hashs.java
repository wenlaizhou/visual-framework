package cn.framework.core.utils;

public final class Hashs {
    
    /**
     * 一致性哈希
     * 
     * @param key k-v对中的key值，key不能为空值
     * @param realCount 真实节点的数量
     * @param ringSize 哈希环的节点数量
     * @return 返回key所在的节点位置，从0开始计数，返回-1为参数错误
     */
    public final static int consistentHash(final String key, final int realCount, final int ringSize) {
        if (!(Strings.isNotNullOrEmpty(key) && realCount > 0 && ringSize > realCount))
            return -1;
        int[] realNodes = new int[realCount];
        final int factor = ringSize / realCount;
        for (int i = 0; i < realCount; i++)
            realNodes[i] = factor * (i + 1);
        final int keyFactor = Math.abs(key.hashCode()) % ringSize;
        for (int pos = 0; pos < realCount; pos++)
            if (realNodes[pos] > keyFactor)
                return pos;
        return 0;
    }
}
