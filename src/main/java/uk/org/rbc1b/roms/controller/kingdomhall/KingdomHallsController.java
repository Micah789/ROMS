/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.org.rbc1b.roms.controller.kingdomhall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import uk.org.rbc1b.roms.controller.common.datatable.AjaxDataTableResult;
import uk.org.rbc1b.roms.db.kingdomhall.KingdomHall;
import uk.org.rbc1b.roms.db.kingdomhall.KingdomHallDao;

/**
 * Controller for the kingdom hall related pages.
 *
 * @author oliver.elder.esq
 */
@Controller
@RequestMapping("/kingdom-halls")
public class KingdomHallsController {

    private static final String BASE_URI = "/kingdom-halls/";
    private KingdomHallDao kingdomHallDao;

    /**
     * Generate the uri used to access the kingdom hall pages.
     *
     * @param kingdomHallId optional kingdom hall id
     * @return uri
     */
    public static String generateUri(Integer kingdomHallId) {
        return kingdomHallId != null ? BASE_URI + kingdomHallId : BASE_URI;
    }

    /**
     * Display the list of kingdom halls.
     *
     * @param model mvc model
     * @return view
     */
    @RequestMapping(method = RequestMethod.GET, headers = "Accept=text/html")
    public String handleList(ModelMap model) {

        model.addAttribute("kingdomHalls", createKingdomHallListModels(kingdomHallDao.findKingdomHalls()));
        model.addAttribute("newUri", generateUri(null) + "new");
        return "kingdom-halls/list";
    }

    /**
     * Display the page list of kingdom halls, returning JSON.
     *
     * @param echoId request identifier returned back to the caller
     * @return view
     */
    @RequestMapping(method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public AjaxDataTableResult<KingdomHallListModel> handlePageList(@RequestParam(value = "sEcho") String echoId) {
        AjaxDataTableResult<KingdomHallListModel> result = new AjaxDataTableResult<KingdomHallListModel>();
        result.setRecords(createKingdomHallListModels(kingdomHallDao.findKingdomHalls()));
        result.setTotalDisplayRecords(result.getAaData().size());
        result.setTotalRecords(result.getAaData().size());
        result.setEcho(echoId);
        return result;
    }

    /**
     * Display a specified kingdom hall.
     *
     * @param kingdomHallId kingdom hall id (primary key)
     * @param model mvc model
     * @return view name
     * @throws NoSuchRequestHandlingMethodException on failure to look up the kingdom hall
     */
    @RequestMapping(value = "{kingdomHallId}", method = RequestMethod.GET)
    public String handleKingdomHall(@PathVariable Integer kingdomHallId, ModelMap model) throws NoSuchRequestHandlingMethodException {

        KingdomHall kingdomHall = kingdomHallDao.findKingdomHall(kingdomHallId);

        if (kingdomHall == null) {
            throw new NoSuchRequestHandlingMethodException("No kingdom hall #" + kingdomHallId, this.getClass());
        }

        model.addAttribute("kingdomHall", kingdomHall);

        return "kingdom-halls/show";
    }

    /**
     * Display the form to create a new circuit.
     *
     * @param model mvc model
     * @return view name
     */
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String handleNewForm(ModelMap model) {

        // initialise the form bean
        model.addAttribute("kingdomHall", new KingdomHallForm());

        return "kingdom-halls/edit";
    }

    /**
     * Create a new kingdom hall.
     *
     * @param kingdomHallForm form bean
     * @return view name
     */
    @RequestMapping(method = RequestMethod.POST)
    public String handleNewSubmit(@Valid KingdomHallForm kingdomHallForm) {

        KingdomHall kingdomHall = new KingdomHall();
        kingdomHall.setName(kingdomHallForm.getName());

        kingdomHallDao.createKingdomHall(kingdomHall);

        return "redirect:kingdom-halls/" + kingdomHall.getKingdomHallId();
    }

    private List<KingdomHallListModel> createKingdomHallListModels(List<KingdomHall> halls) {
        if (CollectionUtils.isEmpty(halls)) {
            return Collections.emptyList();
        }
        List<KingdomHallListModel> modelList = new ArrayList<KingdomHallListModel>(halls.size());
        for (KingdomHall hall : halls) {
            KingdomHallListModel model = new KingdomHallListModel();
            model.setKingdomHallId(hall.getKingdomHallId());
            model.setName(hall.getName());
            model.setPostCode(hall.getAddress().getPostcode());
            model.setTown(hall.getAddress().getTown());
            model.setUri(generateUri(hall.getKingdomHallId()));
            model.setEditUri(generateUri(hall.getKingdomHallId()) + "/edit");
            modelList.add(model);
        }
        return modelList;
    }

    @Autowired
    public void setKingdomHallDao(KingdomHallDao kingdomHallDao) {
        this.kingdomHallDao = kingdomHallDao;
    }
}
