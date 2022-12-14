package chiralsoftware.cmi2w.controllers;

import chiralsoftware.cmi2w.entities.ContactRecord;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import static java.time.Instant.ofEpochMilli;
import java.time.LocalDateTime;
import static java.time.LocalDateTime.ofInstant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import static java.time.format.DateTimeFormatter.ofPattern;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 */
@Controller
public class StatisticsController {

    private static final Logger LOG = Logger.getLogger(StatisticsController.class.getName());
    
    @PersistenceContext
    private EntityManager entityManager;    
    
    @GetMapping("/secure/statistics")
    public String getStatistics(Model model) {
        final LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        final Query q = entityManager.
                createQuery("from ContactRecord cr where cr.contactTime >= :startTime", ContactRecord.class).
                setParameter("startTime", oneYearAgo.toInstant(ZoneOffset.UTC).toEpochMilli());
        final List<ContactRecord> records = q.getResultList();
        // find calls by month
        // i love streams apparently
//        final Map<Month,Long> contactsByMonth =
//                records.stream().collect(
//                        groupingBy(cr -> ofInstant(ofEpochMilli(cr.getContactTime()), ZoneId.systemDefault()).getMonth(),
//                                counting()));
        final DateTimeFormatter formatter = ofPattern("yyyy MM");
        final SortedMap<String,Long> byMonth =
                records.stream().collect(
                        groupingBy(cr -> 
                            ofInstant(ofEpochMilli(cr.getContactTime()), ZoneId.systemDefault()).format(formatter),
                                TreeMap::new,
                                counting()));
        model.addAttribute("byMonth", byMonth);
        
        // find unique contacts by month too
        final SortedMap<String,Set<Long>> uniqueByMonth =
                records.stream().collect(
                        groupingBy(cr -> 
                                ofInstant(ofEpochMilli(cr.getContactTime()), ZoneId.systemDefault()).format(formatter),
                                TreeMap::new,
                                Collectors.mapping(ContactRecord::getContactId, Collectors.toSet())));
        model.addAttribute("uniqueByMonth", uniqueByMonth); // this is a really ugly way to do this
        
        return "secure/statistics";
    }
    
}
