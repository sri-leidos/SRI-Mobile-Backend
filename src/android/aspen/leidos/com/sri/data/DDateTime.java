
package android.aspen.leidos.com.sri.data;
//
// This file was generated by the BinaryNotes compiler.
// See http://bnotes.sourceforge.net 
// Any modifications to this file will be lost upon recompilation of the source ASN.1. 
//

import org.bn.CoderFactory;
import org.bn.annotations.ASN1Element;
import org.bn.annotations.ASN1PreparedElement;
import org.bn.annotations.ASN1Sequence;
import org.bn.coders.IASN1PreparedElement;
import org.bn.coders.IASN1PreparedElementData;




    @ASN1PreparedElement
    @ASN1Sequence ( name = "DDateTime", isSet = false )
    public class DDateTime implements IASN1PreparedElement {
            
        @ASN1Element ( name = "year", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private DYear year = null;
                
  
        @ASN1Element ( name = "month", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private DMonth month = null;
                
  
        @ASN1Element ( name = "day", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private DDay day = null;
                
  
        @ASN1Element ( name = "hour", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private DHour hour = null;
                
  
        @ASN1Element ( name = "minute", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private DMinute minute = null;
                
  
        @ASN1Element ( name = "second", isOptional =  true , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private DSecond second = null;
                
  
        
        public DYear getYear () {
            return this.year;
        }

        
        public boolean isYearPresent () {
            return this.year != null;
        }
        

        public void setYear (DYear value) {
            this.year = value;
        }
        
  
        
        public DMonth getMonth () {
            return this.month;
        }

        
        public boolean isMonthPresent () {
            return this.month != null;
        }
        

        public void setMonth (DMonth value) {
            this.month = value;
        }
        
  
        
        public DDay getDay () {
            return this.day;
        }

        
        public boolean isDayPresent () {
            return this.day != null;
        }
        

        public void setDay (DDay value) {
            this.day = value;
        }
        
  
        
        public DHour getHour () {
            return this.hour;
        }

        
        public boolean isHourPresent () {
            return this.hour != null;
        }
        

        public void setHour (DHour value) {
            this.hour = value;
        }
        
  
        
        public DMinute getMinute () {
            return this.minute;
        }

        
        public boolean isMinutePresent () {
            return this.minute != null;
        }
        

        public void setMinute (DMinute value) {
            this.minute = value;
        }
        
  
        
        public DSecond getSecond () {
            return this.second;
        }

        
        public boolean isSecondPresent () {
            return this.second != null;
        }
        

        public void setSecond (DSecond value) {
            this.second = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(DDateTime.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            