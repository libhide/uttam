package com.ratik.uttam.ui.main;

import com.ratik.uttam.data.DataStore;
import com.ratik.uttam.model.Photo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import io.reactivex.Completable;
import io.reactivex.Single;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Ratik on 24/10/17.
 */
public class MainPresenterImplTest {

    @Rule
    public RxSchedulerRule rule = new RxSchedulerRule();

    @Mock
    private
    MainContract.View view;

    @Mock
    private
    DataStore dataStore;

    private MainPresenterImpl presenter;

    private Photo testPhoto;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        presenter = new MainPresenterImpl(dataStore);
        presenter.setView(view);

        testPhoto = new Photo();
    }

    @Test
    public void shouldLoadPhotoIntoView() {
        // given
        when(dataStore.getPhoto()).thenReturn(Single.just(testPhoto));

        // when
        presenter.getPhoto();

        // then
        verify(view).showPhoto(testPhoto);
        verify(view, never()).onGetPhotoFailed();
    }

    @Test
    public void shouldShowErrorView() {
        // given
        when(dataStore.getPhoto()).thenReturn(Single.error(new IOException()));

        // when
        presenter.getPhoto();

        // then
        verify(view).onGetPhotoFailed();
        verify(view, never()).showPhoto(testPhoto);
    }

    @Test
    public void shouldSavePhotoSuccessfully() {
        // given
        when(dataStore.putPhoto(testPhoto)).thenReturn(Completable.complete());

        // when
        presenter.putPhoto(testPhoto);

        // then
        verify(dataStore).putPhoto(testPhoto);
    }
}