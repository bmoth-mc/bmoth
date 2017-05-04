package de.bmoth.App;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;

/**
 * Created by Julia on 04.05.2017.
 */

@XmlRootElement
public class PersonalPreference {

    private String prefdir;
    private String lastFile;

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

    int prefID;


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


    public static void savePrefToFile(PersonalPreference personalPreference){

        try {
            File file = new File(System.getProperty("user.dir").concat("/src/main/resources/de/bmoth/App/pref.xml"));

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

            File file = new File(System.getProperty("user.dir").concat("/src/main/resources/de/bmoth/App/pref.xml"));
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
