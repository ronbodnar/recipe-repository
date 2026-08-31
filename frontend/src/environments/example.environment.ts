export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1',
  cdnUrl: 'cloudfront or other url prefix for images',
  keycloak: {
    realm: 'master',
    url: 'http://localhost:9000',
    clientId: 'recipe-repository',
  },
};
