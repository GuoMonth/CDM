// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See License.txt in the project root for license information.

package com.microsoft.commondatamodel.objectmodel.cdm.projections;

import com.microsoft.commondatamodel.objectmodel.cdm.CdmAttribute;
import com.microsoft.commondatamodel.objectmodel.cdm.CdmCorpusContext;
import com.microsoft.commondatamodel.objectmodel.cdm.CdmTypeAttributeDefinition;
import com.microsoft.commondatamodel.objectmodel.utilities.StringUtils;
import com.microsoft.commondatamodel.objectmodel.utilities.logger.Logger;

/**
 * Class for attribute cardinality
 */
public class CardinalitySettings {
    // By default all attributes in CDM are Not Nullable and hence setting the default value to be 1:1
    private static final int defaultMinimum = 1;
    private static final int defaultMaximum = 1;
    private final int infiniteMaximum = -1;

    private int _minimumNumber = defaultMinimum;
    private int _maximumNumber = defaultMaximum;
    private String _minimum;
    private String _maximum;

    private CdmCorpusContext ctx;
    private CdmAttribute owner;

    /**
     * CardinalitySettings constructor
     */
    public CardinalitySettings(CdmAttribute owner) {
        this.owner = owner;
        this.ctx = (owner != null) ? owner.getCtx() : null;
    }

    /**
     * Minimum cardinality (range -->> "0" .. "n")
     *
     * @deprecated This function is extremely likely to be removed in the public interface, and not meant
     * to be called externally at all. Please refrain from using it.
     */
    @Deprecated
    public int getMinimumNumber() {
        return _minimumNumber;
    }

    /**
     * @deprecated This function is extremely likely to be removed in the public interface, and not meant
     * to be called externally at all. Please refrain from using it.
     */
    @Deprecated
    public void setMinimumNumber(final int minimumNumber) {
        _minimumNumber = minimumNumber;
    }

    /**
     * Maximum cardinality (range -->> "1" .. "*")
     *
     * @deprecated This function is extremely likely to be removed in the public interface, and not meant
     * to be called externally at all. Please refrain from using it.
     */
    @Deprecated
    public int getMaximumNumber() {
        return _maximumNumber;
    }

    /**
     * @deprecated This function is extremely likely to be removed in the public interface, and not meant
     * to be called externally at all. Please refrain from using it.
     */
    @Deprecated
    public void setMaximumNumber(final int maximumNumber) {
        _maximumNumber = maximumNumber;
    }

    public String getMinimum() {
        return _minimum;
    }

    public void setMinimum(final String minimum) {
        if (!CardinalitySettings.isMinimumValid(minimum)) {
            Logger.error(CardinalitySettings.class.getSimpleName(), this.ctx, Logger.format("Invalid minimum cardinality {0}.", minimum));
        } else {
            _minimum = minimum;
            _minimumNumber = getNumber(_minimum, defaultMinimum);

            // In the case of type attributes, a '0' minimum cardinality represents a nullable attribute
            if (this.owner != null && this.owner instanceof CdmTypeAttributeDefinition) {
                ((CdmTypeAttributeDefinition) this.owner).updateIsNullable((_minimumNumber == 0));
            }
        }
    }

    public String getMaximum() {
        return _maximum;
    }

    public void setMaximum(final String maximum) {
        if (!CardinalitySettings.isMaximumValid(maximum)) {
            Logger.error(CardinalitySettings.class.getSimpleName(), this.ctx, Logger.format("Invalid maximum cardinality {0}.", maximum));
        } else {
            _maximum = maximum;
            _maximumNumber = getNumber(_maximum, defaultMaximum);
        }
    }

    /**
     * Converts the string cardinality to number
     */
    private int getNumber(String value, int defaultValue) {
        if (StringUtils.equalsWithIgnoreCase(value, "*")) {
            return infiniteMaximum;
        }

        try {
            int number = Integer.parseInt(value);
            return number;
        } catch (NumberFormatException e) {
            Logger.error(CardinalitySettings.class.getSimpleName(), this.ctx, Logger.format("Unable to get number for string '{0}'. Falling to default value {1}.", value, defaultValue));
            // defaults to min:max DefaultMinimum:DefaultMaximum in the invalid values
            return defaultValue;
        }
    }

    /**
     * Validate if the minimum cardinality is valid
     * Min Cardinality valid options are as follows -- '0'..Int.MaxValue.ToString()
     * By default Min Cardinality is '1'
     *
     * @deprecated This function is extremely likely to be removed in the public interface, and not meant
     * to be called externally at all. Please refrain from using it.
     */
    @Deprecated
    public static boolean isMinimumValid(String minimum) {
        if (!StringUtils.isNullOrTrimEmpty(minimum)) {
            // By default Min Cardinality is 1
            int minNumber;

            try {
                // Min Cardinality valid options are as follows -- '0'..Int.MaxValue.ToString()
                minNumber = Integer.parseInt(minimum);
                return (minNumber >= 0 && minNumber <= Integer.MAX_VALUE);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return false;
    }

    /**
     * Validate if the maximum cardinality is valid
     * Max Cardinality valid options are as follows -- '1'..Int.MaxValue.ToString(), or can be '*' to define Infinity
     * By default Max Cardinality is '1'
     *
     * @deprecated This function is extremely likely to be removed in the public interface, and not meant
     * to be called externally at all. Please refrain from using it.
     */
    @Deprecated
    public static boolean isMaximumValid(String maximum) {
        if (!StringUtils.isNullOrTrimEmpty(maximum)) {
            // By default Max Cardinality is 1
            int maxNumber;

            // Max Cardinality can be '*' to define Infinity
            // If not '*', an explicit value can be provided, but is limited to '1'..Int.MaxValue.ToString()
            if (StringUtils.equalsWithIgnoreCase(maximum, "*")) {
                return true;
            }

            try {
                maxNumber = Integer.parseInt(maximum);
                return (maxNumber >= defaultMaximum && maxNumber <= Integer.MAX_VALUE);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return false;
    }
}
