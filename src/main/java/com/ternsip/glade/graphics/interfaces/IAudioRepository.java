package com.ternsip.glade.graphics.interfaces;

import com.ternsip.glade.common.logic.LazyWrapper;
import com.ternsip.glade.graphics.display.AudioRepository;

public interface IAudioRepository {

    LazyWrapper<AudioRepository> AUDIO_REPOSITORY = new LazyWrapper<>(AudioRepository::new);

    default AudioRepository getAudioRepository() {
        return AUDIO_REPOSITORY.getObjective();
    }

}
