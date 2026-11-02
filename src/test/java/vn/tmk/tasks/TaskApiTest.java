package vn.tmk.tasks;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskApiTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired TaskRepository repository;
    @BeforeEach void clear() { repository.deleteAll(); }
    private String payload(String status) {
        return "{\"title\":\"Learn Docker\",\"description\":\"Build an image\",\"status\":\"" + status + "\"}";
    }
    @Test void completeLifecycle() throws Exception {
        var response = mvc.perform(post("/api/tasks").contentType("application/json").content(payload("TODO")))
            .andExpect(status().isCreated()).andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.createdAt").isNotEmpty()).andReturn();
        long id = json.readTree(response.getResponse().getContentAsString()).get("id").asLong();
        String createdAt = json.readTree(response.getResponse().getContentAsString()).get("createdAt").asText();
        mvc.perform(get("/api/tasks/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Learn Docker"))
            .andExpect(jsonPath("$.createdAt").value(createdAt));
        mvc.perform(put("/api/tasks/" + id).contentType("application/json").content(payload("DONE")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("DONE"));
        mvc.perform(get("/api/tasks").param("status", "DONE")).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(get("/api/tasks").param("status", "TODO")).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(0)));
        mvc.perform(delete("/api/tasks/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/tasks/" + id)).andExpect(status().isNotFound());
    }
    @Test void validatesInputAndPagination() throws Exception {
        for (String body : new String[]{"{}", "{\"title\":\"  \",\"status\":\"TODO\"}", payload("INVALID"), "{"}) {
            mvc.perform(post("/api/tasks").contentType("application/json").content(body)).andExpect(status().isBadRequest());
        }
        mvc.perform(get("/api/tasks").param("size", "101")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/tasks").param("page", "-1")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/tasks").param("status", "INVALID")).andExpect(status().isBadRequest());
    }
    @Test void missingTasksReturn404() throws Exception {
        mvc.perform(put("/api/tasks/99999").contentType("application/json").content(payload("TODO"))).andExpect(status().isNotFound());
        mvc.perform(delete("/api/tasks/99999")).andExpect(status().isNotFound());
    }
    @Test void paginatesAndSortsNewestFirst() throws Exception {
        mvc.perform(post("/api/tasks").contentType("application/json").content(payload("TODO"))).andExpect(status().isCreated());
        mvc.perform(post("/api/tasks").contentType("application/json").content(payload("DONE"))).andExpect(status().isCreated());
        mvc.perform(get("/api/tasks").param("size", "1")).andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1))).andExpect(jsonPath("$.content[0].status").value("DONE"))
            .andExpect(jsonPath("$.totalElements").value(2)).andExpect(jsonPath("$.totalPages").value(2));
    }
    @Test void databaseHealthIsAvailable() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));
    }
}
