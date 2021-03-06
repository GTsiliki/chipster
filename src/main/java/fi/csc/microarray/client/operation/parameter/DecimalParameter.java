package fi.csc.microarray.client.operation.parameter;

import java.math.BigDecimal;

/**
 * A parameter which takes float values.
 * 
 * @author Janne Käki
 *
 */
public class DecimalParameter extends Parameter {

	private Float minValue;
	private Float maxValue;
	private Float value;
	
	/**
	 * Creates a new DecimalParameter with the given initial values.
	 * 
	 * @param name The name of the parameter.
	 * @param minValue The minimum allowed value of the parameter.
	 * @param maxValue The maximum allowed value of the parameter.
	 * @param initValue The initial value of the parameter.
	 * @throws IllegalArgumentException If the three initial values aren't
	 * 		   logically coherent (minimum value is bigger than maximum value,
	 * 		   or the init value is not between these limits).
	 */
	public DecimalParameter(
			String id, String displayName, String description, Float minValue, Float maxValue, Float initValue)
					throws IllegalArgumentException {
		super(id, displayName, description);
        
		this.minValue = minValue;
        this.maxValue = maxValue;
		if (initValue == null) {
		    this.value = initValue;
		    return;
		}

		if (maxValue < minValue) {
			throw new IllegalArgumentException("Minimum value for decimal parameter " +
					this.getID() + " cannot be bigger than the maximum value.");
		}
		if (initValue < minValue || initValue > maxValue) {
			throw new IllegalArgumentException("Initial value for decimal parameter " +
					this.getID() + " must be inside given limits.");
		}
		this.value = initValue;
	}
	
	public Float getMinValue() {
		return minValue;
	}
	
	public Float getMaxValue() {
		return maxValue;
	}
	
	public Float getDecimalValue() {
		return value;
	}
	
	@Override
	public Object getValue() {
		return value;  // autowrapping really works, it seems :)
	}

	public void setMinValue(float newMinValue) throws IllegalArgumentException {
		if (newMinValue > this.maxValue) {
			throw new IllegalArgumentException("New minimum value for " +
					this.getID() + " cannot exceed current maximum value.");
		}
		this.minValue = newMinValue;
		if (this.value < this.minValue) {
			this.value = this.minValue;
		}
	}
	
	public void setMaxValue(float newMaxValue) throws IllegalArgumentException {
		if (newMaxValue < this.minValue) {
			throw new IllegalArgumentException("New maximum value for " +
					this.getID() + " cannot fall below current minimum value.");
		}
		this.maxValue = newMaxValue;
		if (this.value > this.maxValue) {
			this.value = this.maxValue;
		}
	}
	
	/**
	 * A method for setting the value as a pure decimal number, without the
	 * wrapping object around it.
	 * 
	 * @param newValue The new value for this parameter.
	 * @throws IllegalArgumentException If the suggested value was not within
	 * 		   the defined minimum and maximum limits.
	 */
	public void setDecimalValue(float newValue) throws IllegalArgumentException {
		if (newValue < minValue || newValue > maxValue) {
			throw new IllegalArgumentException("New value for decimal parameter " +
					this.getID() + " must be inside given limits.");
		}
		this.value = newValue;
	}
	
	@Override
	public void setValue(Object newValue) {
	    if (newValue == null) {
	        this.value = null;
	    } else if (newValue instanceof Float) {
			this.value = (Float) newValue;
		} else if (newValue instanceof Double) {
			double doubleValue = (Double) newValue;
			this.value = (float) doubleValue;
		} else {
			throw new IllegalArgumentException(newValue + " is an illegal " +
					"value for decimal parameter " + this.getID() + ".");
		}
	}

	@Override
	public boolean checkValidityOf(Object valueObject) {
	    // Allow null values for unfilled fields
        if (valueObject == null) {
            return true;
        }
	    
		if (valueObject instanceof Float) {
			float floatValue = (Float) valueObject;
			if (floatValue >= this.minValue && floatValue <= this.maxValue) {
				return true;
			}
		}
		
		if (valueObject instanceof Double) {
			double doubleValue = (Double) valueObject;
			if (doubleValue >= this.minValue && doubleValue <= this.maxValue) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		return this.getID() + ": " + value;
	}

	@Override
	public String getValueAsJava() {
		return "" + value;
	}

	@Override
	public void parseValue(String stringValue) throws IllegalArgumentException {
        
        // Empty string means that no value is set
        // This is possible for non-required parameters
        if (stringValue == null || stringValue.equals("")) {
            setValue(null);
            return;
        }
        
        // Otherwise string should represent a decimal
		try {
			setValue(Double.parseDouble(stringValue));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("cannot parse String value \"" + stringValue + "\"");
		}
	}

	@Override
	public String getValueAsString() {
	    return value != null ? new BigDecimal(value.toString()).toPlainString() : "";
	}
}
