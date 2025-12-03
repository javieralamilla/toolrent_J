import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Box,
    Card,
    CardContent,
    Typography,
    TextField,
    Button,
    Alert,
    CircularProgress,
    FormControl,
    InputLabel,
    Select,
    MenuItem
} from '@mui/material';
import { ArrowBack as ArrowBackIcon } from '@mui/icons-material';
import toolService from '../services/tool.service';
import categoryService from '../services/category.service'; // Importar servicio de categorías

const AddExistingTool = () => {
    const navigate = useNavigate(); // Hook de navegación
    
    const [formData, setFormData] = useState({
        name: '',
        category: '',
        quantity: 1
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    
    // Estados para los datos del inventario
    const [inventory, setInventory] = useState([]);
    const [availableNames, setAvailableNames] = useState([]);
    const [availableCategories, setAvailableCategories] = useState([]);
    const [allCategories, setAllCategories] = useState([]); // Todas las categorías del backend
    const [loadingInventory, setLoadingInventory] = useState(true);
    const [loadingCategories, setLoadingCategories] = useState(true);
    const [errorCategories, setErrorCategories] = useState('');

    // Cargar el inventario y categorías al montar el componente
    useEffect(() => {
        loadInventory();
        loadCategories();
    }, []);

    const loadInventory = async () => {
        setLoadingInventory(true);
        try {
            const response = await toolService.getInventory();
            setInventory(response.data);
            
            // Extraer nombres únicos
            const uniqueNames = [...new Set(response.data.map(item => item.name))];
            setAvailableNames(uniqueNames.sort());
            
        } catch (err) {
            console.error('Error al cargar inventario:', err);
            setError('Error al cargar el inventario de herramientas');
        } finally {
            setLoadingInventory(false);
        }
    };

    // Cargar categorías desde el backend
    const loadCategories = async () => {
        setLoadingCategories(true);
        try {
            const response = await categoryService.getAllCategories();
            // Ajusta según como backend devuelve los datos
            const categories = response.data.data || response.data;
            setAllCategories(categories);
            setErrorCategories('');
        } catch (err) {
            console.error('Error al cargar categorías:', err);
            setErrorCategories('No se pudieron cargar las categorías');
            setAllCategories([]);
        } finally {
            setLoadingCategories(false);
        }
    };

    // Cuando se selecciona un nombre, habilitar todas las categorías
    const handleNameChange = (e) => {
        const selectedName = e.target.value;
        
        setFormData(prev => ({
            ...prev,
            name: selectedName,
            category: '' // Resetear categoría cuando cambia el nombre
        }));

        // Usar todas las categorías disponibles (como en AddNewTool)
        setAvailableCategories(allCategories);
    };

    const handleCategoryChange = (e) => {
        setFormData(prev => ({
            ...prev,
            category: e.target.value
        }));
    };

    const handleQuantityChange = (e) => {
        setFormData(prev => ({
            ...prev,
            quantity: e.target.value
        }));
    };

    const handleSubmit = async () => {
        // Validaciones
        if (!formData.name || !formData.category) {
            setError('Por favor selecciona nombre y categoría');
            return;
        }

        if (formData.quantity < 1) {
            setError('La cantidad debe ser mayor a 0');
            return;
        }

        setLoading(true);
        setError('');

        try {
            // Encontrar el objeto categoría completo por ID (para enviar al backend)
            const selectedCategory = allCategories.find(cat => cat.id.toString() === formData.category.toString());
            
            await toolService.saveRegisteredTool(
                { 
                    name: formData.name, 
                    category: selectedCategory // Enviar el objeto completo CategoryEntity
                },
                parseInt(formData.quantity)
            );
            
            // Mostrar mensaje de éxito y redirigir
            alert('Stock actualizado exitosamente');
            navigate('/addTool');
            
        } catch (err) {
            console.error('Error completo:', err);
            
            if (err.response) {
                setError(err.response.data || 'Error al actualizar stock');
            } else if (err.request) {
                setError('No se pudo conectar con el servidor');
            } else {
                setError('Error al procesar la solicitud');
            }
        } finally {
            setLoading(false);
        }
    };

    if (loadingInventory || loadingCategories) {
        return (
            <Box sx={{ p: 3 }}>
                <Card>
                    <CardContent sx={{ p: 4, display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: 200 }}>
                        <CircularProgress />
                    </CardContent>
                </Card>
            </Box>
        );
    }

    if (availableNames.length === 0) {
        return (
            <Box sx={{ p: 3 }}>
                <Button
                    startIcon={<ArrowBackIcon />}
                    onClick={() => navigate('/addTool')}
                    sx={{ 
                        mb: 3,
                        color: '#0A142E',
                        '&:hover': {
                            backgroundColor: 'rgba(10, 20, 46, 0.05)'
                        }
                    }}
                >
                    Volver a opciones
                </Button>
                
                <Card>
                    <CardContent sx={{ p: 4 }}>
                        <Alert severity="warning">
                            No hay herramientas registradas en el sistema. Por favor, agrega una herramienta nueva primero.
                        </Alert>
                    </CardContent>
                </Card>
            </Box>
        );
    }

    return (
        <Box sx={{ p: 3 }}>
            {/* Botón para volver */}
            <Button
                startIcon={<ArrowBackIcon />}
                onClick={() => navigate('/addTool')}
                sx={{ 
                    mb: 3,
                    color: '#0A142E',
                    '&:hover': {
                        backgroundColor: 'rgba(10, 20, 46, 0.05)'
                    }
                }}
            >
                Volver a opciones
            </Button>

            <Card>
                <CardContent sx={{ p: 4 }}>
                    <Typography variant="h5" gutterBottom sx={{ color: '#0A142E', fontWeight: 'bold', mb: 3 }}>
                        Agregar Stock a Herramienta Existente
                    </Typography>
                    
                    {error && (
                        <Alert severity="error" sx={{ mb: 3 }}>
                            {error}
                        </Alert>
                    )}

                    {errorCategories && (
                        <Alert severity="warning" sx={{ mb: 3 }}>
                            {errorCategories}
                        </Alert>
                    )}

                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
                        {/* Select de Nombre */}
                        <FormControl fullWidth required>
                            <InputLabel id="name-label">Nombre de la herramienta</InputLabel>
                            <Select
                                labelId="name-label"
                                id="name-select"
                                value={formData.name}
                                onChange={handleNameChange}
                                label="Nombre de la herramienta"
                            >
                                <MenuItem value="">
                                    <em>-- Selecciona una herramienta --</em>
                                </MenuItem>
                                {availableNames.map((name) => (
                                    <MenuItem key={name} value={name}>
                                        {name}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>

                        {/* Select de Categoría - Dinámicas del backend */}
                        <FormControl fullWidth required disabled={!formData.name}>
                            <InputLabel id="category-label">Categoría</InputLabel>
                            <Select
                                labelId="category-label"
                                id="category-select"
                                value={formData.category}
                                onChange={handleCategoryChange}
                                label="Categoría"
                            >
                                <MenuItem value="">
                                    <em>-- Selecciona una categoría --</em>
                                </MenuItem>
                                {/* Mapear todas las categorías disponibles (como en AddNewTool) */}
                                {availableCategories.map((category) => (
                                    <MenuItem key={category.id} value={category.id}>
                                        {category.name}
                                    </MenuItem>
                                ))}
                            </Select>
                            {!formData.name && (
                                <Typography variant="caption" sx={{ color: '#666', mt: 1, display: 'block' }}>
                                    Primero selecciona un nombre de herramienta
                                </Typography>
                            )}
                        </FormControl>

                        {/* Campo de Cantidad */}
                        <TextField
                            fullWidth
                            type="number"
                            label="Cantidad a agregar"
                            name="quantity"
                            value={formData.quantity}
                            onChange={handleQuantityChange}
                            required
                            inputProps={{ min: 1 }}
                        />

                        {/* Información adicional */}
                        <Alert severity="info">
                            Esta opción es para agregar más unidades de una herramienta que ya existe en el sistema.
                            {formData.name && formData.category && (
                                <Typography variant="body2" sx={{ mt: 1, fontWeight: 'bold' }}>
                                    Seleccionado: {formData.name} - {
                                        (() => {
                                            const selectedCategory = allCategories.find(cat => cat.id.toString() === formData.category.toString());
                                            return selectedCategory ? selectedCategory.name : formData.category;
                                        })()
                                    }
                                </Typography>
                            )}
                        </Alert>

                        {/* Botones */}
                        <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end', mt: 2 }}>
                            <Button
                                variant="outlined"
                                onClick={() => navigate('/addTool')}
                                disabled={loading}
                            >
                                Cancelar
                            </Button>
                            <Button
                                variant="contained"
                                onClick={handleSubmit}
                                disabled={loading || !formData.name || !formData.category}
                                sx={{
                                    backgroundColor: '#FACC15',
                                    color: '#0A142E',
                                    '&:hover': {
                                        backgroundColor: '#e6b800'
                                    },
                                    '&:disabled': {
                                        backgroundColor: '#e0e0e0',
                                        color: '#9e9e9e'
                                    }
                                }}
                            >
                                {loading ? <CircularProgress size={24} sx={{ color: '#0A142E' }} /> : 'Agregar Stock'}
                            </Button>
                        </Box>
                    </Box>
                </CardContent>
            </Card>
        </Box>
    );
};

export default AddExistingTool;