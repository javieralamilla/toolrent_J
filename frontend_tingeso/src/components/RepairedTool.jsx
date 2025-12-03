import React, { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TextField,
    Button,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogContentText,
    DialogActions,
    Alert,
    CircularProgress,
    Chip
} from '@mui/material';
import {
    Build as BuildIcon,
    Search as SearchIcon,
    CheckCircle as CheckCircleIcon
} from '@mui/icons-material';
import ToolService from '../services/tool.service';

const RepairedTool = () => {
    const [tools, setTools] = useState([]);
    const [filteredTools, setFilteredTools] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchId, setSearchId] = useState('');
    const [selectedTool, setSelectedTool] = useState(null);
    const [openDialog, setOpenDialog] = useState(false);
    const [alert, setAlert] = useState({ show: false, type: '', message: '' });
    const [processingRepair, setProcessingRepair] = useState(false);

    useEffect(() => {
        loadToolsInRepair();
    }, []);

    useEffect(() => {
        if (searchId.trim() === '') {
            setFilteredTools(tools);
        } else {
            const filtered = tools.filter(tool => 
                tool.id.toString().includes(searchId)
            );
            setFilteredTools(filtered);
        }
    }, [searchId, tools]);

    const loadToolsInRepair = async () => {
    try {
        setLoading(true);
        const response = await ToolService.getToolsInRepairWithMinorDamage();
        setTools(response.data);
        setFilteredTools(response.data);
        setLoading(false);
    } catch (error) {
        console.error('Error al cargar herramientas:', error);
        showAlert('error', 'Error al cargar las herramientas en reparación con daño leve');
        setLoading(false);
    }
};

    const handleOpenDialog = (tool) => {
        setSelectedTool(tool);
        setOpenDialog(true);
    };

    const handleCloseDialog = () => {
        setOpenDialog(false);
        setSelectedTool(null);
    };

    const handleConfirmRepair = async () => {
        if (!selectedTool) return;

        try {
            setProcessingRepair(true);
            await ToolService.repairedTool(selectedTool.id);
            showAlert('success', `La herramienta ${selectedTool.name} ahora está disponible`);
            handleCloseDialog();
            loadToolsInRepair(); // Recargar la lista
        } catch (error) {
            console.error('Error al marcar herramienta como reparada:', error);
            showAlert('error', 'Error al procesar la reparación de la herramienta');
        } finally {
            setProcessingRepair(false);
        }
    };

    const showAlert = (type, message) => {
        setAlert({ show: true, type, message });
        setTimeout(() => {
            setAlert({ show: false, type: '', message: '' });
        }, 5000);
    };

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom sx={{ color: '#0A142E', fontWeight: 'bold', mb: 4 }}>
                Herramientas Reparadas
            </Typography>

            {alert.show && (
                <Alert severity={alert.type} sx={{ mb: 3 }}>
                    {alert.message}
                </Alert>
            )}

            {/* Filtro por ID */}
            <Paper sx={{ p: 3, mb: 3 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    <SearchIcon sx={{ color: '#0A142E' }} />
                    <TextField
                        fullWidth
                        label="Buscar por ID de herramienta"
                        variant="outlined"
                        value={searchId}
                        onChange={(e) => setSearchId(e.target.value)}
                        placeholder="Ingrese el ID de la herramienta"
                    />
                </Box>
            </Paper>

            {/* Tabla de herramientas */}
            {loading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
                    <CircularProgress />
                </Box>
            ) : filteredTools.length === 0 ? (
                <Paper sx={{ p: 4, textAlign: 'center' }}>
                    <BuildIcon sx={{ fontSize: 64, color: '#ccc', mb: 2 }} />
                    <Typography variant="h6" color="textSecondary">
                        {searchId ? 'No se encontraron herramientas con ese ID' : 'No hay herramientas en reparación con daño leve'}
                    </Typography>
                </Paper>
            ) : (
                <TableContainer component={Paper}>
                    <Table>
                        <TableHead sx={{ backgroundColor: '#0A142E' }}>
                            <TableRow>
                                <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>ID</TableCell>
                                <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Nombre</TableCell>
                                <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Categoría</TableCell>
                                <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Estado</TableCell>
                                <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Acción</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {filteredTools.map((tool) => (
                                <TableRow 
                                    key={tool.id}
                                    sx={{ 
                                        '&:hover': { backgroundColor: '#f5f5f5' },
                                        transition: 'background-color 0.2s'
                                    }}
                                >
                                    <TableCell>{tool.id}</TableCell>
                                    <TableCell>{tool.name}</TableCell>
                                    <TableCell>{tool.category?.name || 'Sin categoría'}</TableCell>
                                    <TableCell>
                                        <Chip 
                                            label="en reparación" 
                                            color="warning"
                                            size="small"
                                            icon={<BuildIcon />}
                                        />
                                    </TableCell>
                                    <TableCell>
                                        <Button
                                            variant="contained"
                                            startIcon={<CheckCircleIcon />}
                                            onClick={() => handleOpenDialog(tool)}
                                            sx={{
                                                backgroundColor: '#0A142E',
                                                '&:hover': {
                                                    backgroundColor: '#FACC15',
                                                    color: '#0A142E'
                                                }
                                            }}
                                        >
                                            Marcar como Reparada
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}

            {/* Dialog de confirmación */}
            <Dialog
                open={openDialog}
                onClose={handleCloseDialog}
                maxWidth="sm"
                fullWidth
            >
                <DialogTitle sx={{ backgroundColor: '#0A142E', color: 'white' }}>
                    Confirmar Reparación
                </DialogTitle>
                <DialogContent sx={{ mt: 2 }}>
                    {selectedTool && (
                        <>
                            <DialogContentText sx={{ mb: 2 }}>
                                ¿Está seguro que desea marcar esta herramienta como reparada y disponible?
                            </DialogContentText>
                            
                            <Paper sx={{ p: 2, backgroundColor: '#f5f5f5' }}>
                                <Typography variant="subtitle2" color="textSecondary" gutterBottom>
                                    Datos de la Herramienta:
                                </Typography>
                                <Box sx={{ mt: 1 }}>
                                    <Typography variant="body2">
                                        <strong>ID:</strong> {selectedTool.id}
                                    </Typography>
                                    <Typography variant="body2">
                                        <strong>Nombre:</strong> {selectedTool.name}
                                    </Typography>
                                    <Typography variant="body2">
                                        <strong>Categoría:</strong> {selectedTool.category?.name || 'Sin categoría'}
                                    </Typography>
                                    <Typography variant="body2">
                                        <strong>Estado Actual:</strong> en reparación
                                    </Typography>
                                </Box>
                            </Paper>

                            <Alert severity="info" sx={{ mt: 2 }}>
                                Al confirmar, esta herramienta pasará al estado DISPONIBLE y podrá ser prestada nuevamente.
                            </Alert>
                        </>
                    )}
                </DialogContent>
                <DialogActions sx={{ p: 2 }}>
                    <Button 
                        onClick={handleCloseDialog}
                        disabled={processingRepair}
                    >
                        Cancelar
                    </Button>
                    <Button
                        onClick={handleConfirmRepair}
                        variant="contained"
                        disabled={processingRepair}
                        startIcon={processingRepair ? <CircularProgress size={20} /> : <CheckCircleIcon />}
                        sx={{
                            backgroundColor: '#FACC15',
                            color: '#0A142E',
                            '&:hover': {
                                backgroundColor: '#0A142E',
                                color: 'white'
                            }
                        }}
                    >
                        {processingRepair ? 'Procesando...' : 'Confirmar'}
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default RepairedTool;