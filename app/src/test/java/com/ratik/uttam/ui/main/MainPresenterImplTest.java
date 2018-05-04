package com.ratik.uttam.ui.main;

import com.ratik.uttam.data.PhotoStore;
import com.ratik.uttam.model.Photo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

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
    private MainContract.View view;

    @Mock
    private PhotoStore photoStore;

    private MainPresenterImpl presenter;

    private Photo testPhoto;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        presenter = new MainPresenterImpl(photoStore);
        presenter.setView(view);

        testPhoto = new Photo.Builder().build();
    }

    @Test
    public void shouldLoadPhotoIntoView() {
        // given
        when(photoStore.getPhoto()).thenReturn(Single.just(testPhoto));

        // when
        presenter.getPhoto();

        // then
        verify(view).showPhoto(testPhoto);
        verify(view, never()).onGetPhotoFailed();
    }

    @Test
    public void shouldShowErrorView() {
        // given
        when(photoStore.getPhoto()).thenReturn(Single.error(new IOException()));

        // when
        presenter.getPhoto();

        // then
        verify(view).onGetPhotoFailed();
        verify(view, never()).showPhoto(testPhoto);
    }
}