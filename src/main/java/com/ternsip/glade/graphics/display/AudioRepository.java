package com.ternsip.glade.graphics.display;

import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.universe.audio.Sound;
import com.ternsip.glade.universe.common.Universal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.joml.Vector3fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.libc.LibCStdlib.free;

@Getter
public class AudioRepository implements Universal {

    private static FloatBuffer ORIENTATION_BUFFER = BufferUtils.createFloatBuffer(6);

    private final long device;
    private final long context;
    private final Map<File, SoundData> fileToSoundPointer = new HashMap<>();

    public AudioRepository() {

        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        this.device = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        this.context = alcCreateContext(this.device, attributes);
        alcMakeContextCurrent(this.context);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(this.device);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

    }

    public void playSound(Sound sound) {
        SoundData soundData = getFileToSoundPointer().computeIfAbsent(sound.getFile(), this::loadSound);
        alSourcef(soundData.getSourcePointer(), AL_GAIN, sound.getMagnitude());
        alSource3f(soundData.getSourcePointer(), AL_POSITION, sound.getPosition().x(), sound.getPosition().y(), sound.getPosition().z());
        alSourcePlay(soundData.getSourcePointer());
    }

    public void update() {
        getUniverse().getSoundRepository().getSounds().removeIf(sound -> {
            playSound(sound);
            return true;
        });

        // Orient listener in 3d space
        Vector3fc pos = getUniverse().getSoundRepository().getListenerPosition();
        Vector3fc orientFront = getUniverse().getSoundRepository().getOrientationFront();
        Vector3fc orientUp = getUniverse().getSoundRepository().getOrientationUp();
        ORIENTATION_BUFFER.clear();
        ORIENTATION_BUFFER.put(orientFront.x());
        ORIENTATION_BUFFER.put(orientFront.y());
        ORIENTATION_BUFFER.put(orientFront.z());
        ORIENTATION_BUFFER.put(orientUp.x());
        ORIENTATION_BUFFER.put(orientUp.y());
        ORIENTATION_BUFFER.put(orientUp.z());
        ORIENTATION_BUFFER.rewind();
        alListener3f(AL_POSITION, pos.x(), pos.y(), pos.z());
        alListenerfv(AL_ORIENTATION, ORIENTATION_BUFFER);

    }


    public void finish() {
        getFileToSoundPointer().values().forEach(soundData -> {
            alDeleteBuffers(soundData.getBufferPointer());
            alDeleteSources(soundData.getSourcePointer());
        });
        alcDestroyContext(getContext());
        alcCloseDevice(getDevice());
    }

    @SneakyThrows
    public SoundData loadSound(File file) {

        ShortBuffer rawAudioBuffer;
        int channels;
        int sampleRate;

        try (MemoryStack stack = stackPush()) {
            //Allocate space to store return information from the function
            IntBuffer channelsBuffer = stack.mallocInt(1);
            IntBuffer sampleRateBuffer = stack.mallocInt(1);

            rawAudioBuffer = stb_vorbis_decode_memory(Utils.loadResourceToByteBuffer(file), channelsBuffer, sampleRateBuffer);

            //Retreive the extra information that was stored in the buffers by the function
            channels = channelsBuffer.get(0);
            sampleRate = sampleRateBuffer.get(0);
        }

        int format = -1;
        if (channels == 1) {
            format = AL_FORMAT_MONO16;
        } else if (channels == 2) {
            format = AL_FORMAT_STEREO16;
        }

        int bufferPointer = alGenBuffers();

        alBufferData(bufferPointer, format, rawAudioBuffer, sampleRate);

        free(rawAudioBuffer);

        int sourcePointer = alGenSources();
        alSourcei(sourcePointer, AL_BUFFER, bufferPointer);

        return new SoundData(bufferPointer, sourcePointer);
    }

    @RequiredArgsConstructor
    @Getter
    public static class SoundData {

        private final int bufferPointer;
        private final int sourcePointer;

    }

}
