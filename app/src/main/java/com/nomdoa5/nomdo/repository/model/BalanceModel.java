package com.nomdoa5.nomdo.repository.model;

public class BalanceModel {
    String id;
    String keterangan;
    String income;
    String outcome;

    public BalanceModel(String id, String keterangan, String income, String outcome) {
        this.id = id;
        this.keterangan = keterangan;
        this.income = income;
        this.outcome = outcome;
    }

    public String getId() {
        return id;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getIncome() {
        return income;
    }

    public String getOutcome() {
        return outcome;
    }
}
