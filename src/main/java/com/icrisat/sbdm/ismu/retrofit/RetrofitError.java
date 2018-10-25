package com.icrisat.sbdm.ismu.retrofit;

import java.util.List;

public class RetrofitError {

    private List<Errors> errors;

    public void setErrors(List<Errors> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        if (errors != null)
            return "errors=" + errors.get(0).getMessage();
        else return "";
    }

    class Errors {
        private String message;

        String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
