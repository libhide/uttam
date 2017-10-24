package com.ratik.uttam.ui.main;

import com.ratik.uttam.data.PhotoRepository;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.ui.settings.SettingsActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    PhotoRepository repository;

    Photo testPhoto;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        presenter = new MainPresenterImpl(repository);
        presenter.setView(view);

        testPhoto = new Photo();
    }

    @Test
    public void shouldLoadPhotoIntoView() throws Exception {
        // arrange
        when(repository.getPhoto()).thenReturn(testPhoto);

        // act
        presenter.loadPhoto();

        // assert
        verify(view).displayPhoto(testPhoto);
    }

    @Test
    public void shouldBeAbleToStoreAPhotoInTheRepository() throws Exception {
        // arrange
        when(repository.getPhoto()).thenReturn(testPhoto);

        // act
        presenter.setPhoto(testPhoto);

        // assert
        assertEquals(testPhoto, repository.getPhoto());
    }

    @Test
    public void shouldOpenSettingsWhenAskedTo() throws Exception {
        // Arrange
        Class settings = SettingsActivity.class;

        // Act
        presenter.launchSettings(settings);

        // Assert
        Mockito.verify(view).showSettings(settings);
    }
}