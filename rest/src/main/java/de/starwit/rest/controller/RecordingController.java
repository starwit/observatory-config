package de.starwit.rest.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.starwit.service.streamprocessing.StreamSavingService;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@RestController
@RequestMapping(path = "${rest.base-path}/recorder")
@ConditionalOnProperty(name = "spring.data.redis.active", havingValue = "true", matchIfMissing = true)
public class RecordingController {
    static final Logger LOG = LoggerFactory.getLogger(RecordingController.class);

    @Autowired
    private StreamSavingService streamSavingService;

    @Operation(summary = "current streams, to be recorded")
    @GetMapping
    public List<String> getCurrentRecorderStreams() {
        return this.streamSavingService.getStreamsToRecord();
    }

    @Operation(summary = "get all available streams")
    @GetMapping(path = "/all")
    public List<String> getAllAvailableStreams() {
        return this.streamSavingService.getAvailableStreams();
    }

    @Operation(summary = "stop all recordings")
    @PostMapping(path = "/stopall")
    public void stopAllRecordings() {
        this.streamSavingService.stopAllRecordings();
    }

    @Operation(summary = "add a stream to record")
    @PostMapping(path = "/start")
    public void addStreamToRecord(String streamName) {
        this.streamSavingService.addStreamToRecord(streamName);
    }

    @Operation(summary = "remove a stream from recording")
    @PostMapping(path = "/stop")
    public void removeStreamFromRecord(String streamName) {
        this.streamSavingService.removeStreamFromRecord(streamName);
    }
}
