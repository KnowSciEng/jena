/*
 * (c) Copyright 2008 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.tdb.solver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingBase;
import com.hp.hpl.jena.tdb.pgraph.NodeId;
import com.hp.hpl.jena.tdb.pgraph.NodeTable;

/** Bind that delayes turning a NodeId into a Node until explicitly needed by get() */

public class BindingTDB extends BindingBase
{
    private final NodeTable nodeTable ;
    private final BindingNodeId idBinding ;
    private final Map<Var,Node> cache = new HashMap<Var, Node>() ;

    public BindingTDB(Binding parent, BindingNodeId idBinding, NodeTable nodeTable)
    {
        super(parent) ;
        this.idBinding = idBinding ;
        this.nodeTable = nodeTable ;
    }

    @Override
    protected void add1(Var var, Node node)
    { throw new UnsupportedOperationException() ; }

    @Override
    protected int size1() { return idBinding.size(); }
    
    /** Iterate over all the names of variables.
     */
    @Override
    public Iterator<Var> vars1() 
    {
        return idBinding.iterator() ;
    }

    @Override
    public boolean contains1(Var var)
    {
        return idBinding.containsKey(var) ;
    }
    
    @Override
    public Node get1(Var var)
    {
        try {
            Node n = cache.get(var) ;
            if ( n != null )
                return n ;
            
            NodeId id = idBinding.get(var) ;
            if ( id == null )
                return null ; 
            n = nodeTable.retrieveNode(id) ;
            // Update cache.
            cache.put(var, n) ;
            return n ;
        } catch (Exception ex)
        {
            System.err.printf("get1(%s)\n", var) ;
            System.err.println(idBinding) ;
            ex.printStackTrace(System.err) ;
            System.exit(1) ;
            return null ;
        }
    }

    @Override
    protected void checkAdd1(Var var, Node node)
    { throw new UnsupportedOperationException() ; }
}

/*
 * (c) Copyright 2008 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */