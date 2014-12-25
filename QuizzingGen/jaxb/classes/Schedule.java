//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.21 at 08:17:38 PM EDT 
//


package jaxb.classes;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}quizMeet" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="dateGenerated" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="morningStart" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="afternoonStart" type="{http://www.w3.org/2001/XMLSchema}date" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "quizMeet"
})
@XmlRootElement(name = "schedule")
public class Schedule {

    protected List<QuizMeet> quizMeet;
    @XmlAttribute(name = "dateGenerated")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateGenerated;
    @XmlAttribute(name = "morningStart")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar morningStart;
    @XmlAttribute(name = "afternoonStart")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar afternoonStart;

    /**
     * Gets the value of the quizMeet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the quizMeet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQuizMeet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QuizMeet }
     * 
     * 
     */
    public List<QuizMeet> getQuizMeet() {
        if (quizMeet == null) {
            quizMeet = new ArrayList<QuizMeet>();
        }
        return this.quizMeet;
    }

    /**
     * Gets the value of the dateGenerated property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateGenerated() {
        return dateGenerated;
    }

    /**
     * Sets the value of the dateGenerated property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateGenerated(XMLGregorianCalendar value) {
        this.dateGenerated = value;
    }

    /**
     * Gets the value of the morningStart property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMorningStart() {
        return morningStart;
    }

    /**
     * Sets the value of the morningStart property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMorningStart(XMLGregorianCalendar value) {
        this.morningStart = value;
    }

    /**
     * Gets the value of the afternoonStart property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAfternoonStart() {
        return afternoonStart;
    }

    /**
     * Sets the value of the afternoonStart property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAfternoonStart(XMLGregorianCalendar value) {
        this.afternoonStart = value;
    }

}
