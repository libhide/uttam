package com.ratik.uttam.ui.main;

import com.ratik.uttam.data.DataStore;
import com.ratik.uttam.model.Photo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Ratik on 24/10/17.
 */
public class MainPresenterImplTest {

    MainPresenterImpl presenter;

    @Mock
    MainContract.View view;

    @Mock
    DataStore dataStore;

    Photo testPhoto;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        presenter = new MainPresenterImpl(dataStore);
        presenter.setView(view);

        testPhoto = new Photo();
    }

    @Test
    public void shouldLoadPhotoIntoView() throws Exception {
        // arrange
        when(dataStore.getPhoto()).thenReturn(testPhoto);

        // act
        presenter.getPhoto();

        // assert
        verify(view).showPhoto(testPhoto);
    }

    @Test
    public void shouldBeAbleToStoreAPhotoInTheRepository() throws Exception {
        // arrange
        when(dataStore.getPhoto()).thenReturn(testPhoto);

        // act
        presenter.putPhoto(testPhoto);

        // assert
        assertEquals(testPhoto, dataStore.getPhoto());
    }
}