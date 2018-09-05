package com.ratik.uttam.ui.main;

import com.ratik.uttam.data.PhotoStore;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.network.FetchService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Completable;
import io.reactivex.Single;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

    @Mock
    private FetchService service;

    private MainPresenterImpl presenter;
    private Photo testPhoto;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new MainPresenterImpl(photoStore, service);
        presenter.setView(view);
        testPhoto = new Photo.Builder().build();
    }

    @Test
    public void shouldLoadPhotoIntoView() {
        when(photoStore.getPhoto())
                .thenReturn(Single.just(testPhoto));

        presenter.getPhoto();

        verify(view).showPhoto(testPhoto);
        verify(view, never()).onGetPhotoFailed();
    }

    @Test
    public void shouldShowPhotoLoadErrorInView() {
        when(photoStore.getPhoto())
                .thenReturn(Single.error(new Exception()));

        presenter.getPhoto();

        verify(view).onGetPhotoFailed();
        verify(view, never()).showPhoto(testPhoto);
    }

    @Test
    public void viewShouldAttach() {
        presenter.setView(view);
        assertNotNull(presenter.getView());
    }

    @Test
    public void viewShouldDetach() {
        presenter.detachView();
        assertNull(presenter.getView());
    }

    @Test
    public void disposablesShouldDispose() {
        presenter.detachView();
        assertEquals(true, presenter.getCompositeDisposable().isDisposed());
    }

    @Test
    public void shouldRefetchPhotoSuccessfully() {
        when(service.getFetchPhotoCompletable())
                .thenReturn(Completable.complete());

        presenter.refetchPhoto();

        verify(view, times(1)).showRefetchProgress();
        verify(view, times(1)).hideRefetchProgress();
        verify(view, never()).showRefetchError(anyString());
    }

    @Test
    public void shouldRefetchPhotoUnsuccessfully() {
        when(service.getFetchPhotoCompletable())
                .thenReturn(Completable.error(new Throwable()));

        presenter.refetchPhoto();

        verify(view).showRefetchError(anyString());
        verify(view, times(1)).showRefetchProgress();
        verify(view, never()).hideRefetchProgress();

    }
}