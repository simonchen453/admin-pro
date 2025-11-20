package com.adminpro.core.tools.seq;

import com.adminpro.core.base.entity.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 公共序列表 sys_commons_sequence
 *
 * @author simon
 * @date 2018-09-06
 */
public class SequenceEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 表名
     */
    public static final String TABLE_NAME = "SYS_COMMONS_SEQUENCE";

    /**
     * 序列名称
     */
    public static final String COL_SEQ_NAME = "COL_SEQ_NAME";
    /**
     * 序列下一个值
     */
    public static final String COL_SEQ_NEXT_VALUE = "COL_SEQ_NEXT_VALUE";

    /**
     * 序列名称
     */
    private String seqName;
    /**
     * 序列下一个值
     */
    private Long seqNextValue;

    public void setSeqName(String seqName) {
        this.seqName = seqName;
    }

    public String getSeqName() {
        return seqName;
    }

    public void setSeqNextValue(Long seqNextValue) {
        this.seqNextValue = seqNextValue;
    }

    public Long getSeqNextValue() {
        return seqNextValue;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("seqName", getSeqName())
                .append("seqNextValue", getSeqNextValue())
                .toString();
    }
}
