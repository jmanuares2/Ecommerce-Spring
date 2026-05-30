/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useState } from 'react';

// Creamos el contexto para poder compartir favoritos entre componentes
// sin tener que pasar props de padre a hijo todo el tiempo.
const FavoriteContext = createContext();

export function FavoriteProvider({ children }) {
  // Este estado guarda el listado de productos favoritos.
  // Arranca vacio porque al principio el usuario todavia no eligio nada.
  const [favoriteItems, setFavoriteItems] = useState([]);

  // Esta funcion agrega o quita un producto de favoritos.
  // Si el producto ya estaba, lo sacamos. Si no estaba, lo agregamos.
  const addToFavorite = (product) => {
    setFavoriteItems((currentFavorites) => {
      // some devuelve true si encuentra un favorito con el mismo id.
      const isAlreadyFavorite = currentFavorites.some((item) => item.id === product.id);

      // Usamos operador ternario:
      // - true: quitamos el producto con filter
      // - false: agregamos el producto creando un array nuevo
      return isAlreadyFavorite
        ? currentFavorites.filter((item) => item.id !== product.id)
        : [...currentFavorites, product];
    });
  };

  return (
    // Todo componente que este dentro de este Provider puede usar
    // favoriteItems y addToFavorite mediante useFavorite().
    <FavoriteContext.Provider value={{ favoriteItems, addToFavorite }}>
      {children}
    </FavoriteContext.Provider>
  );
}

// Hook propio para usar el contexto de favoritos de forma mas simple.
export const useFavorite = () => useContext(FavoriteContext);
