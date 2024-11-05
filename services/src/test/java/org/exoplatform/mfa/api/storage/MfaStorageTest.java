package org.exoplatform.mfa.api.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import org.exoplatform.commons.api.settings.ExoFeatureService;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.mfa.storage.MfaStorage;
import org.exoplatform.mfa.storage.dao.RevocationRequestDAO;
import org.exoplatform.mfa.storage.dto.RevocationRequest;
import org.exoplatform.portal.branding.BrandingService;
import org.exoplatform.portal.branding.BrandingServiceImpl;
import org.exoplatform.services.resources.ResourceBundleService;

public class MfaStorageTest {
  private PortalContainer container;

  @Mock
  BrandingService brandingService;


  @Mock
  ExoFeatureService featureService;

  @Mock
  ResourceBundleService resourceBundleService;

  @Before
  public void setup() {

    PortalContainer container = PortalContainer.getInstance();

    brandingService = mock(BrandingServiceImpl.class);
    if (container.getComponentInstanceOfType(BrandingService.class) == null) {
      container.registerComponentInstance(BrandingService.class.getName(), brandingService);
    }

    featureService = mock(ExoFeatureService.class);
    if (container.getComponentInstanceOfType(ExoFeatureService.class) == null) {
      container.registerComponentInstance(ExoFeatureService.class.getName(), featureService);
    }

    resourceBundleService = mock(ResourceBundleService.class);
    if (container.getComponentInstanceOfType(ResourceBundleService.class) == null) {
      container.registerComponentInstance(ResourceBundleService.class.getName(), resourceBundleService);
    }

    RequestLifeCycle.begin(container);
  }

  @After
  public void teardown() {
    RevocationRequestDAO revocationRequestDAO = ExoContainerContext.getService(RevocationRequestDAO.class);
    revocationRequestDAO.deleteAll();
    RequestLifeCycle.end();
  }

  @Test
  public void testCreateRevocationRequest() {
    MfaStorage mfaStorage = ExoContainerContext.getService(MfaStorage.class);
    assertNotNull(mfaStorage);

    try {
      mfaStorage.createRevocationRequest(null);
      fail("Shouldn't allow to add null application");
    } catch (IllegalArgumentException e) {
      // Expected
    }

    RevocationRequest revocationRequest = new RevocationRequest(null,"john", "type");

    RevocationRequest storedRequest = mfaStorage.createRevocationRequest(revocationRequest);
    assertNotNull(storedRequest);
    assertNotNull(storedRequest.getId());
    assertEquals(revocationRequest.getUser(), storedRequest.getUser());
    assertEquals(revocationRequest.getType(), storedRequest.getType());
  }

  @Test
  public void testCountByUsernameAndType() {
    MfaStorage mfaStorage = ExoContainerContext.getService(MfaStorage.class);

    String user ="john";
    String type="otp";
    assertEquals(0L,mfaStorage.countByUsernameAndType(user,type));

    RevocationRequest revocationRequest = new RevocationRequest(null,user, type);
    mfaStorage.createRevocationRequest(revocationRequest);
    assertEquals(1L,mfaStorage.countByUsernameAndType(user,type));

    mfaStorage.createRevocationRequest(revocationRequest);
    assertEquals(2L,mfaStorage.countByUsernameAndType(user,type));

  }

  @Test
  public void testDeleteByUsernameAndType() {
    MfaStorage mfaStorage = ExoContainerContext.getService(MfaStorage.class);

    String user ="john";
    String type="otp";
    assertEquals(0L,mfaStorage.countByUsernameAndType(user,type));

    RevocationRequest revocationRequest = new RevocationRequest(null,user, type);
    mfaStorage.createRevocationRequest(revocationRequest);
    assertEquals(1L,mfaStorage.countByUsernameAndType(user,type));

    mfaStorage.createRevocationRequest(revocationRequest);
    assertEquals(2L,mfaStorage.countByUsernameAndType(user,type));

    mfaStorage.deleteRevocationRequest(user,type);
    assertEquals(0L,mfaStorage.countByUsernameAndType(user,type));

  }

  @Test
  public void testDeleteById() {
    MfaStorage mfaStorage = ExoContainerContext.getService(MfaStorage.class);

    String user ="john";
    String user2 ="mary";
    String type="otp";
    assertEquals(0,mfaStorage.findAll().size());

    RevocationRequest revocationRequest = new RevocationRequest(null,user, type);
    revocationRequest=mfaStorage.createRevocationRequest(revocationRequest);
    assertEquals(1,mfaStorage.findAll().size());
    RevocationRequest revocationRequest2 = new RevocationRequest(null,user2, type);
    revocationRequest2=mfaStorage.createRevocationRequest(revocationRequest2);
    assertEquals(2,mfaStorage.findAll().size());

    mfaStorage.deleteById(revocationRequest.getId());
    assertEquals(1,mfaStorage.findAll().size());
    assertEquals(revocationRequest2.getId(),mfaStorage.findAll().get(0).getId());


  }

  @Test
  public void testFindAll() {
    MfaStorage mfaStorage = ExoContainerContext.getService(MfaStorage.class);

    String user1 = "john";
    String user2 = "mary";
    String user3 = "franck";
    String type = "otp";
    assertEquals(0, mfaStorage.findAll().size());

    RevocationRequest revocationRequest = new RevocationRequest(null, user1, type);
    mfaStorage.createRevocationRequest(revocationRequest);
    assertEquals(1, mfaStorage.findAll().size());

    revocationRequest = new RevocationRequest(null, user2, type);
    mfaStorage.createRevocationRequest(revocationRequest);
    assertEquals(2, mfaStorage.findAll().size());

    revocationRequest = new RevocationRequest(null, user3, type);
    mfaStorage.createRevocationRequest(revocationRequest);
    assertEquals(3, mfaStorage.findAll().size());

  }


  @Test
  public void testFindBy() {
    MfaStorage mfaStorage = ExoContainerContext.getService(MfaStorage.class);

    String user1 = "john";
    String type = "otp";
    assertEquals(0, mfaStorage.findAll().size());

    RevocationRequest revocationRequest = new RevocationRequest(null, user1, type);
    revocationRequest = mfaStorage.createRevocationRequest(revocationRequest);
    assertEquals(1, mfaStorage.findAll().size());

    assertEquals(revocationRequest.getId(), mfaStorage.findById(revocationRequest.getId()).getId());


  }
}
