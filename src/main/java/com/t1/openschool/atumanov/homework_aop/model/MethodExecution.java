package com.t1.openschool.atumanov.homework_aop.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table
public class MethodExecution {
    @Id
    private Long id;
    @Column("name")
    private String fullyQualifiedName;
    @Column("duration")
    private Long nanoDuration;
    @Column("async")
    private boolean isAsync;

    public MethodExecution(String fullyQualifiedName, Long nanoDuration, boolean isAsync){
        this.fullyQualifiedName = fullyQualifiedName;
        this.nanoDuration = nanoDuration;
        this.isAsync = isAsync;
    }
}
