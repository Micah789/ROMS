/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.org.rbc1b.roms.controller.qualification;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import uk.org.rbc1b.roms.db.volunteer.Qualification;
import uk.org.rbc1b.roms.db.volunteer.QualificationDao;

/**
 * Qualification types which may be applied to a volunteer.
 *
 * @author Tina
 */
@Controller
@RequestMapping("/qualifications")
public class QualificationsController {

    private QualificationDao qualificationDao;

    /**
     * @param model spring mvc model
     * @return model containing the list of qualifications
     */
    @RequestMapping(method = RequestMethod.GET)
    public String handleList(ModelMap model) {

        model.addAttribute("qualifications", qualificationDao.findQualifications());

        return "qualifications/list";
    }

    /**
     * @param name qualification primary key
     * @param model spring mvc model
     * @return mvc view name
     * @throws NoSuchRequestHandlingMethodException 404 response
     */
    @RequestMapping(value = "{name}", method = RequestMethod.GET)
    public String handleQualification(@PathVariable String name, ModelMap model) throws NoSuchRequestHandlingMethodException {

        Qualification qualification = qualificationDao.findQualification(name);

        if (qualification == null) {
            throw new NoSuchRequestHandlingMethodException("No qualification with name [" + name + "]", this.getClass());
        }

        model.addAttribute("qualification", qualification);

        return "qualifications/show";
    }

    /**
     * Display the form to create a new qualification.
     *
     * @param model mvc model
     * @return mvc view name
     */
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String handleNewForm(ModelMap model) {

        // initialise the form bean
        model.addAttribute("qualification", new QualificationForm());

        return "qualifications/update";
    }

    /**
     * Create a new qualification.
     *
     * @param qualificationForm form bean
     * @return mvc redirect
     */
    @RequestMapping(method = RequestMethod.POST)
    public String handleNewSubmit(@Valid QualificationForm qualificationForm) {

        Qualification qualification = new Qualification();
        qualification.setName(qualificationForm.getName());
        qualification.setDescription(qualificationForm.getDescription());

        //qualification.setQualifcations(); - no qualifications initially created

        qualificationDao.createQualification(qualification);

        return "redirect:qualifications/" + qualificationForm.getName();
    }

    @Autowired
    public void setQualificationDao(QualificationDao qualificationDao) {
        this.qualificationDao = qualificationDao;
    }
}
