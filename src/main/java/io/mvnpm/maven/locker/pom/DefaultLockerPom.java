package io.mvnpm.maven.locker.pom;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;

import com.google.common.io.Resources;

import io.mvnpm.maven.locker.model.Artifacts;
import io.mvnpm.maven.locker.model.GAV;
import io.quarkus.qute.Qute;

public final class DefaultLockerPom implements LockerPom {

    private final LockerPomFileAccessor dependenciesLockFile;
    private final GAV gav;
    private final Log log;

    private DefaultLockerPom(
            LockerPomFileAccessor dependenciesLockFile, GAV gav, Log log) {
        this.dependenciesLockFile = dependenciesLockFile;
        this.gav = gav;
        this.log = log;
    }

    public static LockerPom from(
            LockerPomFileAccessor dependenciesLockFile, GAV gav, Log log) {
        return new DefaultLockerPom(
                requireNonNull(dependenciesLockFile), requireNonNull(gav), requireNonNull(log));
    }

    @Override
    public void write(Artifacts artifacts) {
        try {
            URL url = Resources.getResource(this.getClass(), "pom.xml");
            String template = Resources.toString(url, StandardCharsets.UTF_8);
            final String fmted = Qute.fmt(template, makeDataModel(gav, artifacts));
            try (Writer writer = dependenciesLockFile.writer()) {
                writer.write(fmted);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Map<String, Object> makeDataModel(GAV gav, Artifacts artifacts) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("pom", gav);
        dataModel.put("dependencies", artifacts);
        return dataModel;
    }

    @Override
    public Artifacts read() {
        return Artifacts.fromArtifacts(LockerPomReader.read(dependenciesLockFile.file));
    }
}
