#include <al.h>
#include <alc.h>
#include <cstdio>
#include <stdio.h>
#include <process.h>

#include "file_encrypt.h"

#define    AUDIOBUFFER_SIZE    1024*64
#define    NUMBER_OF_AUDIOBUFFERS    3


struct SoundInfo
{
	ALuint source;
	ALuint frequency;
	ALenum format;
	int dataSize;
	int time;
	int max_time;
	Crypt *c;
	bool continue_thread;
} *s_i;


class BufferPlayer
{
    public:
        void change_time(int);
        int play();
        void stop();
        void exit();
        void replay();
        void continue_playing();

        BufferPlayer(Crypt& cd) : c(cd)
        {
        }

        //static SoundInfo *s_i;

    private:
        static void buffer_filling(void*);

        DWORD size,chunkSize;
        short formatType,channels;
        DWORD sampleRate,avgBytesPerSec;
        short bytesPerSample,bitsPerSample;
        DWORD dataSize;
        ALCdevice *device;
        ALCcontext *context;
        Crypt& c;

        ALuint source;
        ALuint buffers[NUMBER_OF_AUDIOBUFFERS];
        ALuint frequency;
        ALenum format;
};

void BufferPlayer::change_time(int time)
{
    s_i->time = ((double)time / 100.0) * (s_i->max_time);
}

void BufferPlayer::buffer_filling(void* id)
{
    for(s_i->time = NUMBER_OF_AUDIOBUFFERS; s_i->time < s_i->max_time && s_i->continue_thread;)
    {
        ALuint buffer = 0;
        alSourceUnqueueBuffers(s_i->source, 1, &buffer);
        if(buffer == 0)
            continue;
        s_i->c->decrypt_memory(44 + s_i->time*AUDIOBUFFER_SIZE, AUDIOBUFFER_SIZE);
        alBufferData(buffer, s_i->format, s_i->c->buffer, AUDIOBUFFER_SIZE, s_i->frequency);
        alSourceQueueBuffers(s_i->source, 1, &buffer);
        s_i->time++;
    }
    s_i->continue_thread = false;
}

int BufferPlayer::play()
{
    c.decrypt_memory(0, 44);
    if(c.buffer[0]!='R' || c.buffer[1]!='I' || c.buffer[2]!='F' || c.buffer[3]!='F')
        throw logic_error("No RIFF");
    size = u32endian(c.buffer + 4);
    if (c.buffer[8]!='W' || c.buffer[9]!='A' || c.buffer[10]!='V' || c.buffer[11]!='E')
        throw logic_error("not WAVE");
    if (c.buffer[12]!='f' || c.buffer[13]!='m' || c.buffer[14]!='t' || c.buffer[15]!=' ')
        throw logic_error("not fmt ");

    chunkSize = u32endian(c.buffer + 16);
    formatType =  u16endian(c.buffer + 20);
    channels  = u16endian(c.buffer + 22);
    sampleRate  = u32endian(c.buffer + 24);
    avgBytesPerSec = u32endian(c.buffer + 28);
    bytesPerSample  = u16endian(c.buffer + 32);
    bitsPerSample  = u16endian(c.buffer + 34);
    if (c.buffer[36]!='d' || c.buffer[37]!='a' || c.buffer[38]!='t' || c.buffer[39]!='a')
        throw logic_error("Missing DATA");

    dataSize = u32endian(c.buffer + 40);

    cout << "Chunk Size: " << chunkSize << "\n";
    cout << "Format Type: " << formatType << "\n";
    cout << "Channels: " << channels << "\n";
    cout << "Sample Rate: " << sampleRate << "\n";
    cout << "Average Bytes Per Second: " << avgBytesPerSec << "\n";
    cout << "Bytes Per Sample: " << bytesPerSample << "\n";
    cout << "Bits Per Sample: " << bitsPerSample << "\n";
    cout << "Data Size: " << dataSize << "\n";

    device = alcOpenDevice(NULL);
    if(!device)
        throw logic_error("no sound device");
    context = alcCreateContext(device, NULL);
    alcMakeContextCurrent(context);
    if(!context)
        throw logic_error("no sound context");

    alGenBuffers(NUMBER_OF_AUDIOBUFFERS, buffers);
    if(alGetError() != AL_NO_ERROR)
        throw logic_error("Error GenBuffers");
    alGenSources(1, &source);
    if(alGetError() != AL_NO_ERROR)
        throw logic_error("Error GenSource");

    frequency=sampleRate;
    format = 0;

    if(bitsPerSample == 8)
    {
        if(channels == 1)
            format = AL_FORMAT_MONO8;
        else if(channels == 2)
            format = AL_FORMAT_STEREO8;
    }
    else if(bitsPerSample == 16)
    {
        if(channels == 1)
            format = AL_FORMAT_MONO16;
        else if(channels == 2)
            format = AL_FORMAT_STEREO16;
    }
    if(!format)
        throw logic_error("Wrong BitPerSample");

    for(int i=0; i<NUMBER_OF_AUDIOBUFFERS; i++)
    {
        c.decrypt_memory(44 + AUDIOBUFFER_SIZE*i, AUDIOBUFFER_SIZE);
        alBufferData(buffers[i], format, c.buffer, AUDIOBUFFER_SIZE, frequency);
    }

    if(alGetError() != AL_NO_ERROR)
        throw logic_error("Error loading ALBuffer");

    //Sound setting variables
    ALfloat SourcePos[] = { 0.0, 0.0, 0.0 };                                    //Position of the source sound
    ALfloat SourceVel[] = { 0.0, 0.0, 0.0 };                                    //Velocity of the source sound
    ALfloat ListenerPos[] = { 0.0, 0.0, 0.0 };                                  //Position of the listener
    ALfloat ListenerVel[] = { 0.0, 0.0, 0.0 };                                  //Velocity of the listener
    ALfloat ListenerOri[] = { 0.0, 0.0, -1.0,  0.0, 1.0, 0.0 };                 //Orientation of the listener
                                                                                //First direction vector, then vector pointing up)
    //Listener
    alListenerfv(AL_POSITION,    ListenerPos);                                  //Set position of the listener
    alListenerfv(AL_VELOCITY,    ListenerVel);                                  //Set velocity of the listener
    alListenerfv(AL_ORIENTATION, ListenerOri);                                  //Set orientation of the listener

    //Source
    alSourceQueueBuffers(source, NUMBER_OF_AUDIOBUFFERS, buffers);              //Link the buffer to the source
    alSourcef (source, AL_PITCH,    1.0f     );                                 //Set the pitch of the source
    alSourcef (source, AL_GAIN,     1.0f     );                                 //Set the gain of the source
    alSourcefv(source, AL_POSITION, SourcePos);                                 //Set the position of the source
    alSourcefv(source, AL_VELOCITY, SourceVel);                                 //Set the velocity of the source
    alSourcei (source, AL_LOOPING,  AL_FALSE );                                 //Set if source is looping sound

    //PLAY

    alSourcePlay(source);
    if(alGetError() != AL_NO_ERROR)
        throw logic_error("Error playing sound");

    s_i = new SoundInfo;
    s_i->format = format;
    s_i->frequency = frequency;
    s_i->source = source;
    s_i->dataSize = dataSize;
    s_i->c = &c;
    s_i->time = 0;
    s_i->max_time = dataSize/(2*AUDIOBUFFER_SIZE);
    s_i->continue_thread = true;
    _beginthread(buffer_filling, 256, NULL);
}

void BufferPlayer::stop()
{
    alSourcePause(source);
}

void BufferPlayer::replay()
{
    s_i->time = 0;
    if(!s_i->continue_thread)
    {
        s_i->continue_thread = true;
        _beginthread(buffer_filling, 256, NULL);
    }
    alSourcePlay(source);
}

void BufferPlayer::continue_playing()
{
    alSourcePlay(source);
}

void BufferPlayer::exit()
{
    alDeleteSources(1, &source);
    alDeleteBuffers(10, buffers);
    alcMakeContextCurrent(NULL);
    alcDestroyContext(context);
    alcCloseDevice(device);
    s_i->continue_thread = false;
}
