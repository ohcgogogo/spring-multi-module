package com.example.businessjob.batch.job;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

public class ExternalMemberSignupJobParametersValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        if(parameters.getString("from") == null){
            throw new JobParametersInvalidException("from parameters is not found");
        }
        if(parameters.getString("to") == null){
            throw new JobParametersInvalidException("to parameters is not found");
        }
    }
}


