package de.bmoth.app;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;

/**
 * Created by Julian on 04.05.2017.
 */

@XmlRootElement
public class PersonalPreference {

    private String prefdir;
    private String lastFile;


    private int minINT;
    private int maxINT;
    private int maxInitialStates=5;
    private int maxSolution =5;
    int prefID;

    public PersonalPreference() {
        this.prefdir = System.getProperty("user.dir");
    }

    public int getPrefID() {
        return prefID;
    }

    @XmlAttribute
    public void setPrefID(int prefID) {
        this.prefID = prefID;
    }

    public String getPrefdir() {
        return prefdir;
    }

    @XmlElement
    public void setPrefdir(String prefdir) {
        this.prefdir = prefdir;
    }

    public String getLastFile() {
        return lastFile;
    }

    @XmlElement
    public void setLastFile(String lastFile) {
        this.lastFile = lastFile;
    }

    public int getMinINT() {
        return minINT;
    }

    @XmlElement
    public void setMinINT(int minINT) {
        this.minINT = minINT;
    }

    public int getMaxINT() {
        return maxINT;
    }

    @XmlElement
    public void setMaxINT(int maxINT) {
        this.maxINT = maxINT;
    }

    public int getMaxInitialStates() {
        return maxInitialStates;
    }

    @XmlElement
    public void setMaxInitialStates(int maxInitialStates) {
        this.maxInitialStates = maxInitialStates;
    }

    public int getMaxSolution() {
        return maxSolution;
    }

    @XmlElement
    public void setMaxSolution(int maxSolution) {
        this.maxSolution = maxSolution;
    }



    public static void savePrefToFile(PersonalPreference personalPreference) {
        try {
            File file = new File(System.getProperty("user.dir").concat("/src/main/resources/de/bmoth/app/pref.xml"));

            JAXBContext jaxbContext = JAXBContext.newInstance(PersonalPreference.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(personalPreference,file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }


    public static PersonalPreference loadPreferenceFromFile(){
        try {
            File file = new File(System.getProperty("user.dir").concat("/src/main/resources/de/bmoth/app/pref.xml"));
            JAXBContext jaxbContext = JAXBContext.newInstance(PersonalPreference.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            PersonalPreference personalPreference = (PersonalPreference) jaxbUnmarshaller.unmarshal(file);
            return personalPreference;
        } catch (JAXBException e) {
            System.err.println(e.getCause());
            System.err.println("Auto-creating XML-file on next session, default preferences applied");
        }
    return new PersonalPreference();
    }

}
