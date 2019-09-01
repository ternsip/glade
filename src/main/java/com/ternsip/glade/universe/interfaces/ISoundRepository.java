package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.LazyWrapper;
import com.ternsip.glade.universe.audio.SoundRepository;

public interface ISoundRepository {

    LazyWrapper<SoundRepository> SOUND_REPOSITORY = new LazyWrapper<>(SoundRepository::new);

    default SoundRepository getSoundRepository() {
        return SOUND_REPOSITORY.getObjective();
    }

}
