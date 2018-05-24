package com.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "fitnessapp-mobilehub-969335156-diary")

public class DiaryDO {
    private String _userId;
    private String _entryId;
    private String _description;
    private Double _energy;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBRangeKey(attributeName = "entryId")
    @DynamoDBAttribute(attributeName = "entryId")
    public String getEntryId() {
        return _entryId;
    }

    public void setEntryId(final String _entryId) {
        this._entryId = _entryId;
    }
    @DynamoDBAttribute(attributeName = "description")
    public String getDescription() {
        return _description;
    }

    public void setDescription(final String _description) {
        this._description = _description;
    }
    @DynamoDBAttribute(attributeName = "energy")
    public Double getEnergy() {
        return _energy;
    }

    public void setEnergy(final Double _energy) {
        this._energy = _energy;
    }

}
